# parsing -- get all folder in experiments

import os
import json

from deepeval import evaluate
from deepeval.metrics import GEval
from deepeval.models import GeminiModel
from deepeval.test_case import LLMTestCaseParams, LLMTestCase
from deepeval.evaluate import CacheConfig
import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
import seaborn as sns
from pandas.plotting import parallel_coordinates, scatter_matrix
from math import pi

def get_folders_in_directory(directory_path):
    """Get all folders in the specified directory."""
    if not os.path.exists(directory_path):
        print(f"Directory '{directory_path}' does not exist")
        return []
    
    # Get all entries in the directory
    all_entries = os.listdir(directory_path)
    
    # Filter out only the directories
    folders = [entry for entry in all_entries 
               if os.path.isdir(os.path.join(directory_path, entry))]
    
    return folders

# Get all folders in experiments/anthropic
experiments_vendor = "jakta-playground/experiments/"
experiments_vendor = get_folders_in_directory(experiments_vendor)

# Dictionary to store UUIDs grouped by model_name
uuids_by_model = {}

# Loop through each vendor
for vendor in experiments_vendor:
    vendor_path = os.path.join("jakta-playground/experiments/", vendor)
    
    # Get all model_name folders for this vendor
    model_names = get_folders_in_directory(vendor_path)
    
    # Loop through each model_name
    for model_name in model_names:
        model_path = os.path.join(vendor_path, model_name)
        
        # Get all experiment_name folders for this model
        experiment_names = get_folders_in_directory(model_path)
        
        # Loop through each experiment_name
        for experiment_name in experiment_names:
            chat_path = os.path.join(model_path, experiment_name, "ExplorerBot", "chat")
            
            # Check if chat directory exists
            if os.path.exists(chat_path):
                # Get all jsonl files (UUIDs)
                chat_files = [f for f in os.listdir(chat_path) if f.endswith('.jsonl')]
                key = vendor + "/" + model_name 
                # Extract UUIDs (remove .jsonl extension)
                uuids = [os.path.splitext(file)[0] for file in chat_files]
                
                # Add UUIDs to the dictionary, grouped by model_name
                if key not in uuids_by_model:
                    uuids_by_model[key] = []
                uuids_by_model[key].append(experiment_name + "/ExplorerBot/chat/" + uuids[0])
    
    # Function to load and parse a JSONL file
def load_jsonl(file_path):
    messages = []
    try:
        with open(file_path, 'r') as f:
            for line in f:
                if line.strip():  # Skip empty lines
                    try:
                        messages.append(json.loads(line))
                    except json.JSONDecodeError as e:
                        print(f"Error parsing JSON in {file_path}: {e}")
    except FileNotFoundError:
        print(f"File not found: {file_path}")
    except Exception as e:
        print(f"Error reading {file_path}: {e}")
    return messages

# Extract chat messages from raw JSONL data
def extract_chat_messages(raw_messages):
    chat_messages = []
    for msg in raw_messages:
        if 'type' in msg and msg['type'] == 'chatMessage' and 'role' in msg and 'content' in msg:
            chat_messages.append({
                'timestamp': msg.get('@timestamp', ''),
                'role': msg.get('role', ''),
                'content': msg.get('content', '')
            })
    return chat_messages

ground_truth = """
EVENT: achieve reach(Object)
CONDITIONS:
  - there_is(Object, here)
OPERATIONS:
  - <none>

EVENT: achieve reach(Object)
CONDITIONS:
  - there_is(Object, Direction)
OPERATIONS:
  - execute move(Direction)

EVENT: achieve reach(Object)
CONDITIONS:
  - not(there_is(Object, _))
OPERATIONS:
  - execute getDirectionToMove(Direction)
  - execute move(Direction)
  - achieve reach(Object)
"""


# Print the results
# Output file path
output_file = "geval_results.json"

def create_styled_bar_plot(data, x_values, y_values, errors=None, title="", xlabel="", ylabel="", 
                           filename=None, show_values=True, show_individuals=True, custom_labels=None):
    """Creates a styled bar plot with consistent aesthetics."""
    plt.style.use('seaborn-v0_8-darkgrid')
    fig, ax = plt.subplots(figsize=(10, 6))
    
    # Bar styling
    bar_width = 0.4
    colors = plt.cm.viridis(np.linspace(0.2, 0.8, len(x_values)))
    
    # Plot bars with error bars if provided
    bars = ax.bar(np.arange(len(x_values)), y_values, bar_width, 
                 capsize=10, alpha=0.8, color=colors,
                 yerr=errors, ecolor='black', 
                 error_kw={'elinewidth': 2, 'capthick': 2})
    
    # Add labels and styling
    ax.set_xlabel(xlabel, fontsize=18, fontweight='bold')
    ax.set_ylabel(ylabel, fontsize=18, fontweight='bold')
    ax.set_title(title, fontsize=20, fontweight='bold', pad=20)
    ax.set_xticks(np.arange(len(x_values)))
    
    # Use custom labels if provided, otherwise use x_values
    if custom_labels:
        ax.set_xticklabels(custom_labels, ha='center', fontsize=16)
    else:
        ax.set_xticklabels(x_values, ha='center', fontsize=16)
    
    # Add value labels above bars
    if show_values:
        for i, bar in enumerate(bars):
            height = bar.get_height()
            plt.text(bar.get_x() + bar.get_width()/2 + 0.05, height, 
                     f"{height:.2f}", ha='left', va='bottom', fontsize=15, fontweight='bold')
    
    # Improve y-axis display
    ax.set_ylim(0, max(y_values) * 1.2)
    ax.yaxis.grid(True, linestyle='--', alpha=0.7)
    ax.tick_params(axis='y', labelsize=16)
    
    # Add styling to the plot frame
    for spine in ax.spines.values():
        spine.set_color('#444444')
        spine.set_linewidth(1.5)
    
    # Add a legend for error bars
    if errors is not None:
        ax.plot([], [], ' ', label='Error bars show standard error')
        ax.legend(loc='upper right', frameon=True, framealpha=0.9, fontsize=14)
    
    # Show individual data points if requested
    if show_individuals and isinstance(data, dict):
        for i, scores in enumerate(data.values()):
            ax.scatter([i] * len(scores), scores, color='black', alpha=0.6, 
                      s=50, zorder=3, label='Individual scores' if i == 0 else "")
    
    # Adjust layout and save if filename provided
    plt.tight_layout()
    if filename:
        plt.savefig(filename, dpi=300, bbox_inches='tight')
    
    return fig, ax

def get_short_model_name(full_name):
    """Converts full model names to shortened display names."""
    if 'gemini-2.5-pro' in full_name or 'gemini-pro-2.5' in full_name:
        return "gemini pro 2.5"
    elif 'deepseek-V3' in full_name:
        return "deepseek V3"
    elif 'gpt-4.1' in full_name:
        return "gpt-4.1"
    elif 'claude-3.7' in full_name:
        return "claude-3.7"
    else:
        # Default case - just take the model name without the vendor
        return full_name.split('/')[-1]

def load_or_compute_geval_results(output_file, uuids_by_model):
    """Loads existing GEval results or computes new ones."""
    if os.path.exists(output_file):
        print(f"Loading existing GEval results from {output_file}")
        with open(output_file, "r") as infile:
            return json.load(infile)
    else:
        
        judge_deep_eval = GeminiModel(
            model_name="gemini-2.5-pro-preview-03-25",
            #model_name="gemini-2.0-flash",
            
            api_key=os.environ.get("GOOGLE_API_KEY"),
        )

        bdi_plan_correctness = GEval(
            name="BDI Plan Correctness and Minimality",
            evaluation_steps=[
                "Extract invented goals, beliefs, and plans from the 'actual output'.",
                "Compare extracted plans against 'expected output' plans for logical equivalence and coverage.",
                "Assess if invented goals/beliefs are necessary or add needless complexity compared to 'expected output'.",
                "Evaluate plan minimality; penalize unnecessary subgoals, conditions, or operations vs 'expected output'.",
                "Verify that operations correctly use specified prefixes (execute, achieve, add, etc.) and admissible actions.",
                "Check if conditions logically correspond to the intended plan activation scenario.",
                "Score based on plan correctness, necessity of inventions, and adherence to minimality principle."
            ],
            evaluation_params=[LLMTestCaseParams.ACTUAL_OUTPUT, LLMTestCaseParams.EXPECTED_OUTPUT],
            model=judge_deep_eval
        )

        print("\nComputing GEval results for models:")
        geval_results = {}
        for model_name, uuids in uuids_by_model.items():
            print(f"\nModel: {model_name}")
            print(f"Total UUIDs: {len(uuids)}")
            test_cases = []
            for uuid in uuids:
                to_load = os.path.join("jakta-playground/experiments/", model_name, uuid + ".jsonl")
                raw = load_jsonl(to_load)
                messages = extract_chat_messages(raw)
                test_case = LLMTestCase(
                    input="",
                    actual_output=messages[-1]["content"],
                    expected_output=ground_truth
                )
                test_cases.append(test_case)
            scores = evaluate(
                test_cases=test_cases,
                metrics=[bdi_plan_correctness],
            )

            flatten = [test.metrics_data[0].score for test in scores.test_results]
            geval_results[model_name] = flatten
        
        # Save the results to a JSON file
        with open(output_file, "w") as outfile:
            json.dump(geval_results, outfile, indent=4)
        
        print(f"GEval results saved to {output_file}")
        return geval_results

def prepare_stats(data_dict):
    """Calculate mean scores and standard errors from a dictionary of scores."""
    means = []
    std_errors = []
    for scores in data_dict.values():
        means.append(np.mean(scores))
        std_dev = np.std(scores, ddof=1)
        std_error = std_dev / np.sqrt(len(scores)) if len(scores) > 1 else 0
        std_errors.append(std_error)
    return means, std_errors

# Column name mapping dictionary
column_name_to_short = {
    "Amount of Generated Plans": "PC",
    "Average amount of belief per plan context": "CC",
    "Average amount of operations per plan body": "PBC",
    "Amount of generated plans which are general/specific (roughly: using variables or not)": "GC",
    "Amount of generated plans which are useless (e.g., not executable at runtime because subsumed my more general plans, etc.)": "RA",
    "Amount of invented goals": "NGC",
    "Amount of invented beliefs": "NBC",
    "Amount of inadequate usage of admissible goals: not properly written, invented but not used, used but not admissible, already admissible": "GSM",
    " Amount of inadequate usage of admissible beliefs: not properly written, invented but not used, used but not admissible, already admissible": "BSM",
    " Amount of inadequate usage of actions": "TODO",
    "Time to goal achievement for the generated plan groups who work (in seconds) (maybe normalized w.r.t. the minimal human-generated plan)": "GAT",
    "Executes (yes/no)": "EC",
    "Reaches destination (yes/no)": "TSR",
    "Number of plans which are correctly converted in JaKtA" : "JPC",
    " Cosine distance of generated plans group w.r.t. minimal, human-derived plans group": "Cosine",
}

# Main execution
# Load or compute GEval results
geval_results = load_or_compute_geval_results(output_file, uuids_by_model)

# Prepare data for GEval plot
model_names = list(geval_results.keys())
avg_scores, std_errors = prepare_stats(geval_results)

# Create the GEval bar plot
create_styled_bar_plot(
    data=geval_results,
    x_values=model_names,
    y_values=avg_scores,
    errors=std_errors,
    title='Average PRAS Score by Model with 95% Confidence Interval',
    xlabel='Model',
    ylabel='PRAS Score',
    custom_labels=[get_short_model_name(name) for name in model_names],
    filename='geval_results_bar_plot.png'
)

# Process CSV data
df = pd.read_csv("PGP_evaluation_AgentSpeak_plan_generation.csv")
df.rename(columns=column_name_to_short, inplace=True)
df["Model"] = df["Model"].str.replace(r"\s*\(.*?\)", "", regex=True)

# Convert yes/no columns to binary
yes_no_columns = ["EC", "TSR"]
for col in yes_no_columns:
    df[col] = df[col].apply(lambda v: 1 if v == 'y' else (0 if v == 'n' else v))
    df[col] = df[col] * 100 # Percentage representation

# drop cosine 
df.drop(columns=["Cosine"], inplace=True)
print(df)
# write a latex table with all the metrics
fix_columns = ["CC", "PBC", "GAT"] 
# try to replce , to .
for col in fix_columns:
    df[col] = df[col].apply(lambda v: str(v).replace(",", "."))
    df[col] = pd.to_numeric(df[col], errors='coerce')
# group by model
df_grouped = df.groupby("Model").mean().reset_index()
# Convert to LaTeX table
# Select these columns for the LaTeX table
cols_to_display = ["Model", "PC", "CC", "PBC", "GC", "RA", "NGC", "NBC", "GSM", "BSM", "TSR", "GAT"]
df_grouped = df_grouped[cols_to_display]
latex_table = df_grouped.to_latex(index=False, float_format="%.2f", escape=False)
print(latex_table)

metric_labels = {
    'PC': 'Plan Count',
    'CC': 'Context Complexity',
    'PBC': 'Plan Body Complexity', 
    'GC': 'Generalization Count',
    'RA': 'Redundancy Amount',
    'NGC': 'Novel Goals Count',
    'NBC': 'Novel Beliefs Count',
    'GSM': 'Goal Semantic Misalignment',
    'BSM': 'Belief Semantic Misalignment',
    'TSR': 'Task Success Rate (%)',
    'GAT': 'Goal Achievement Time (s)'
}
    
metrics_to_maximize = ['GC', 'TSR']
metrics_to_minimize = ['RA', 'GSM', 'BSM']
neutral_metrics = ['PC', 'CC', 'PBC', 'NGC', 'NBC']
# Create visualizations for plan generation metrics
def plot_plan_metrics_comparison(df, metrics_to_plot=None):
    """
    Creates an improved visualization for plan generation metrics across models.
    Each metric has its own subplot with color coding to indicate if higher or lower is better.
    """
    # Define metric categories
    
    
    # Full names for metrics
 
    # Filter available metrics
    available_columns = [col for col in df.columns if col != 'Model']
    if metrics_to_plot:
        available_metrics = [m for m in metrics_to_plot if m in available_columns]
    else:
        available_metrics = available_columns
    
    # Define colors for different metric types
    color_map = {
        'maximize': '#7ad151',  # Green for metrics to maximize
        'minimize': '#482475',  # Purple for metrics to minimize
        'neutral': '#25858e'    # Blue for neutral metrics
    }
    
    # Get models
    models = df['Model'].tolist()
    x = np.arange(len(models))
    
    # Create figure with subplots
    n_metrics = len(available_metrics)
    n_cols = min(4, n_metrics)
    n_rows = (n_metrics + n_cols - 1) // n_cols
    
    fig, axes = plt.subplots(n_rows, n_cols, figsize=(15, 4*n_rows))
    axes = np.array(axes).flatten() if n_metrics > 1 else [axes]
    
    # Plot each metric in its own subplot
    for i, metric in enumerate(available_metrics):
        ax = axes[i]
        
        # Determine color and direction based on metric type
        if metric in metrics_to_maximize:
            color = color_map['maximize']
            direction = "↑ Higher is better"
        elif metric in metrics_to_minimize:
            color = color_map['minimize']
            direction = "↓ Lower is better"
        else:
            color = color_map['neutral']
            direction = ""
        
        # Create bars
        bars = ax.bar(x, df[metric], color=color, alpha=0.8)
        
        # Add value labels on top of bars
        for j, bar in enumerate(bars):
            height = bar.get_height()
            ax.text(bar.get_x() + bar.get_width()/2, height * 1.02,
                    f'{height:.2f}', ha='center', va='bottom', fontsize=9,
                    fontweight='bold')
        
        # Set title and labels
        metric_name = metric_labels.get(metric, metric)
        ax.set_title(f"{metric_name}\n{direction}", fontsize=12)
        ax.set_xticks(x)
        ax.set_xticklabels(models, rotation=45, ha='right', fontsize=10)
        
        # Add grid for easier reading
        ax.grid(axis='y', linestyle='--', alpha=0.3)
        
        # Y-axis starts at 0
        ax.set_ylim(bottom=0)
    
    # Hide empty subplots if any
    for j in range(i+1, len(axes)):
        axes[j].set_visible(False)
    
    # Add overall title
    plt.suptitle('Plan Generation Metrics by Model', fontsize=16, fontweight='bold')
    
    # Adjust layout
    plt.tight_layout(rect=[0, 0, 1, 0.96])
    plt.savefig('plan_metrics_comparison.pdf', dpi=300, bbox_inches='tight')
    
    return fig


metrics_to_plot = ['PC', 'CC', 'PBC', 'NGC', 'NBC', 'RA', 'GSM', 'BSM', 'GC', 'TSR']
# Create the visualization
plot_plan_metrics_comparison(df_grouped, metrics_to_plot)
