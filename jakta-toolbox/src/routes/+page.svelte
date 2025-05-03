<script lang="ts">
	import { Tile } from 'carbon-components-svelte'
</script>

<main class="p-20 pt-0">
	<Tile>
		<h1 class="bx--data-table-header__title">Experiment Viewer</h1>
		<p class="bx--data-table-header__description">
			This application provides a specialized viewer, featuring two primary interfaces: a Log
			Viewer and a Chat Viewer. The Chat Viewer allows reviewing generation logs with full
			markdown and code block rendering, making it ideal for analyzing the generated plans,
			debugging conversations, or reviewing the plan generation procedure. The Log Viewer is useful for
			analyzing the execution traces of agents.
		</p>
	</Tile>
</main>

<style>
	h1 {
		font-size: 1.5rem;
		padding-bottom: 0.4rem;
	}
	p {
		font-size: 1rem;
	}
</style>
