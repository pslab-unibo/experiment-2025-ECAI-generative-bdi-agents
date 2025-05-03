<script lang="ts">
	import { DataTable, Toolbar, ToolbarContent, ToolbarSearch } from 'carbon-components-svelte'
	import type {
		DataTableHeader,
		DataTableRow
	} from 'carbon-components-svelte/src/DataTable/DataTable.svelte'
	import type { LogEntry } from '$lib/types';
	import { loadFileContent } from '$lib/fileLoader'

	const {
		title = 'Test',
		jsonPath = null
	} = $props<{
		title: string,
		jsonPath: string | null
	}>()

	const headers: DataTableHeader[] = [
		{ key: 'level', value: 'Level', width: '9%' },
		{ key: 'message', value: 'Message' }
	]

	let rows = $state<DataTableRow[]>()
	let filteredRowIds = $state<number[]>([])

	let messages = $state<LogEntry[]>([])
	let isLoading = $state(true)
	let error = $state<string | null>(null)

	function filterRows(row: DataTableRow, value: string | number): boolean {
		return String(row.message).toLowerCase().includes(String(value).toLowerCase()) ||
			String(row.level).toLowerCase().includes(String(value).toLowerCase())
	}

	async function fetchTraceData() {
		isLoading = true;
		const result = await loadFileContent<{entries: LogEntry[]}>(jsonPath);
		if (result.data && result.data.entries && Array.isArray(result.data.entries)) {
			messages = result.data.entries;

			// Transform log entries into DataTable rows
			rows = messages.map((entry, index) => ({
				id: String(index),
				level: entry.level,
				message: entry.message
			}));
		} else {
			rows = [];
			messages = [];
		}
		error = result.error;
		isLoading = false;
	}

	$effect(() => {
		if (jsonPath !== null) {
			fetchTraceData();
		}
	})
</script>

<main>
	{#if isLoading}
		<div class="loading">
			<div class="spinner"></div>
			<p>Loading trace data...</p>
		</div>
	{:else if error}
		<div class="p-60 pt-0">
			<div class="error">
				<h1 class="bx--data-table-header__title">No log traces loaded</h1>
				<p class="bx--data-table-header__description">
					Error: {error}
				</p>
			</div>
		</div>
		{:else}
			<DataTable
				size="short"
				title="Trace for {title}"
				description="The execution trace at the implementation level"
				{headers}
				{rows}
			>
				<Toolbar size="sm">
					<ToolbarContent>
						<ToolbarSearch persistent shouldFilterRows={filterRows} bind:filteredRowIds />
					</ToolbarContent>
				</Toolbar>

				<svelte:fragment slot="cell" let:row let:cell>
					{#if cell.key === 'level'}
						<div class:info={row.level === 'INFO'}
								 class:warning={row.level === 'WARN'}
								 class:error={row.level === 'ERROR'}
								 class:debug={row.level === 'DEBUG'}
								 class:level-cell={cell.key === 'level'}>
							{cell.value}
						</div>
					{:else}
						<div>
							{cell.value}
						</div>
						{/if}
				</svelte:fragment>
			</DataTable>
		{/if}
</main>

<style>
		:global(td div) {
				font-size: 1.1rem;
				color: black;
		}

    .level-cell {
        font-size: 1rem;
        border-radius: 2rem;
        display: flex;
        justify-content: center;
    }

    .info {
        background-color: rgba(0, 132, 255, 0.1);
    }

    .warning {
        background-color: rgba(255, 170, 0, 0.15);
    }

    .error {
        background-color: rgba(255, 59, 48, 0.15);
    }

    .debug {
        background-color: rgba(100, 100, 100, 0.1);
    }

    :global(.bx--btn--primary) {
        background-color: rgb(127, 82, 255);
    }


    :global(.bx--btn--primary) {
		background-color: rgb(127, 82, 255);
	}

	:global(.bx--btn--primary:focus) {
		border-color: rgb(107, 59, 248);
		box-shadow:
			inset 0 0 0 1px rgb(107, 59, 248),
			inset 0 0 0 2px #fff;
	}

	:global(.bx--btn--primary:hover) {
		background-color: rgb(107, 59, 248);
	}

	:global(
		.bx--table-toolbar--small .bx--toolbar-search-container-active .bx--search-input:focus,
		.bx--table-toolbar--sm .bx--toolbar-search-container-active .bx--search-input:focus
	) {
		outline: 2px solid rgb(127, 82, 255);
		outline-offset: -2px;
		background-color: #e5e5e5;
	}

	:global(input) {
		--tw-ring-color: rgb(127, 82, 255);
	}
	:global(.bx--toolbar-search-container-persistent .bx--search-input:focus:not([disabled])) {
		outline: 2px solid rgb(127, 82, 255);
		outline-offset: -2px;
	}
</style>
