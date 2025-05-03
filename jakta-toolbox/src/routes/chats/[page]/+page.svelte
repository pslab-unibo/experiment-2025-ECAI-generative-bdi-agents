<script lang="ts">
	import ChatHistory from '../../../components/ChatHistory.svelte'
	import { page } from '$app/state';
	import { getFilenameWithoutExtension } from '$lib/common';

	let filePath = $derived(page.params.page)
	let decodedPath = $derived(decodeURIComponent(filePath))
</script>

<div class="chat-container">
	<ChatHistory
		title={getFilenameWithoutExtension(filePath)}
		jsonPath={decodedPath}
	/>
</div>
