<script lang="ts">
	import ExecutionLog from '../../../components/ExecutionLog.svelte'
	import { page } from '$app/state'
	import { getFilenameWithoutExtension } from '$lib/common';

	let filePath = $derived(page.params.page)
	let decodedPath = $derived(decodeURIComponent(filePath))
</script>

<div class="log-container">
	<ExecutionLog
		title={getFilenameWithoutExtension(filePath)}
		jsonPath={decodedPath}
	/>
</div>
