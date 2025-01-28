client<llm> GPT4o {
  provider openai
  options {
    model "gpt-4o"
    max_tokens 1000
    temperature 0.7
    retry_policy ExponentialBackoffRetry
    api_key env.OPENAI_API_KEY
  }
}

client<llm> FastOpenAI {
  provider openai
  options {
    model "gpt-3.5-turbo"
    max_tokens 1000
    temperature 0.7
    retry_policy ExponentialBackoffRetry
    api_key env.OPENAI_API_KEY
  }
}

client<llm> ClaudeSonnet {
  provider anthropic
  options {
    model "claude-3-5-sonnet-20240620"
    max_tokens 1000
    temperature 0.7
    retry_policy ExponentialBackoffRetry
    api_key env.ANTHROPIC_API_KEY
  }
}

client<llm> FastAnthropic {
  provider anthropic
  options {
    model "claude-3-haiku-20240307"
    max_tokens 1000
    temperature 0.7
    retry_policy ExponentialBackoffRetry
    api_key env.ANTHROPIC_API_KEY
  }
}

client<llm> Mistral {
  provider openai-generic
  options {
    max_tokens 1000
    temperature 0.1
    model mistral
    base_url "https://api-inference.huggingface.co/v1"
    retry_policy ExponentialBackoffRetry
    api_key env.HUGGINGFACE_API_KEY
  }
}

client<llm> Mixstral {
  provider openai-generic
  options {
    max_tokens 1000
    temperature 0.1
    model mixtral
    base_url "https://api-inference.huggingface.co/v1"
    retry_policy ExponentialBackoffRetry
    api_key env.HUGGINGFACE_API_KEY
  }
}

client<llm> Llama2 {
  provider openai-generic
  options {
    max_tokens 1000
    temperature 0.1
    model llama-2
    base_url "https://api-inference.huggingface.co/v1"
    retry_policy ExponentialBackoffRetry
    api_key env.HUGGINGFACE_API_KEY
  }
}

client<llm> Openchat {
  provider openai-generic
  options {
    max_tokens 1000
    temperature 0.1
    model openchat
    base_url "https://api-inference.huggingface.co/v1"
    retry_policy ExponentialBackoffRetry
    api_key env.HUGGINGFACE_API_KEY
  }
}

client<llm> Hermes {
  provider openai-generic
  options {
    max_tokens 1000
    temperature 0.1
    model nous-hermes
    base_url "https://api-inference.huggingface.co/v1"
    retry_policy ExponentialBackoffRetry
    api_key env.HUGGINGFACE_API_KEY
  }
}

client<llm> Gemma {
  provider openai-generic
  options {
    max_tokens 1000
    temperature 0.1
    model gemma
    base_url "https://api-inference.huggingface.co/v1"
    retry_policy ExponentialBackoffRetry
    api_key env.HUGGINGFACE_API_KEY
  }
}

client<llm> HermesLocalNetwork {
  provider openai-generic
  options {
    max_tokens 1500
    temperature 0.0
    base_url "http://192.168.1.52:8080/v1"
    model "DeepSeek-R1-Distill-Qwen-7B-Q4_K_M"
  }
}

client<llm> HermesLocalhost {
  provider openai-generic
  options {
    max_tokens 1500
    temperature 0.0
    base_url "http://llama-cpp:8080/v1"
    model "DeepSeek-R1-Distill-Qwen-7B-Q4_K_M"
  }
}