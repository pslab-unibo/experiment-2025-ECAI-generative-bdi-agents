<script lang="ts">
	import {
		Column,
		Content,
		Grid,
		Header,
		Row,
		SideNav,
		SideNavDivider,
		SideNavItems,
		SideNavLink,
		SkipToContent
	} from 'carbon-components-svelte';
	import '../app.css';
	import 'carbon-components-svelte/css/white.css';
	import { onMount } from 'svelte';
	import { Document, Folder } from 'carbon-icons-svelte';
	import type { FileItem } from '$lib/types';
	import { error } from '@sveltejs/kit';
	import { goto } from '$app/navigation';
	import { PUBLIC_BASE_PATH } from '$env/static/public';

	let { children } = $props();
	let isSideNavOpen = $state(false);
	let currentPath = $state<string>(PUBLIC_BASE_PATH ?? './');
	let files = $state<FileItem[]>([]);
	let isLoading = $state(true);
	let filesLoadingErrorMsg = $state<string | null>(null);
	let fileLoadingErrorMsg = $state<string | null>(null);

	async function fetchFiles(path: string) {
		isLoading = true;
		filesLoadingErrorMsg = null;

		try {
			const response = await fetch(`/api/file-browser?path=${encodeURIComponent(path)}`);
			if (!response.ok) {
				error(500, 'Failed to fetch files');
			}

			files = (await response.json()).filter(
				(item: FileItem) => item.type === 'directory' || item.name.toLowerCase().endsWith('.jsonl')
			);

			currentPath = path;
		} catch (err: any) {
			console.error('Error fetching files:', err);
			filesLoadingErrorMsg = err.message;
		} finally {
			isLoading = false;
		}
	}

	function navigateToDirectory(path: string) {
		fetchFiles(path);
	}

	function navigateUp() {
		const parentPath = currentPath.split('/').slice(0, -1).join('/') || PUBLIC_BASE_PATH;
		fetchFiles(parentPath);
	}

	async function determineFileType(filePath: string): Promise<'chat' | 'log' | 'none'> {
		try {
			const response = await fetch(`/api/file-content?path=${encodeURIComponent(filePath)}`);

			if (!response.ok) {
				return 'none';
			}

			const parsedJson = JSON.parse((await response.text()))
			if(parsedJson.entries[0].role && parsedJson.entries[0].content) {
				return 'chat'
			} else {
				return 'log'
			}
		} catch (error) {
			return 'none'
		}
	}

	async function handleFileClick(path: string) {
		const fileType = await determineFileType(path);

		if (fileType === 'chat') {
			fileLoadingErrorMsg = null
			await goto(`/chats/${encodeURIComponent(path)}`);
		} else if (fileType === 'log') {
			fileLoadingErrorMsg = null
			await goto(`/execution-logs/${encodeURIComponent(path)}`);
		} else {
			fileLoadingErrorMsg = "Invalid JSON Lines file."
		}
	}

	onMount(() => {
		fetchFiles(currentPath);
	});
</script>

<Header
	href="/"
	persistentHamburgerMenu={true}
	company="JaKtA"
	platformName="Experiment Browser"
	bind:isSideNavOpen
>
	<svelte:fragment slot="skip-to-content">
		<SkipToContent />
	</svelte:fragment>
</Header>

<SideNav bind:isOpen={isSideNavOpen}>
	<SideNavItems>
		<SideNavLink text="File Explorer" />
		<SideNavDivider />

		{#if filesLoadingErrorMsg}
			<SideNavLink text="Error loading files" />
		{:else if isLoading}
			<SideNavLink text="Loading..." />
		{:else}
			{#if currentPath !== (PUBLIC_BASE_PATH ?? './')}
				<SideNavLink text="↩ Up" on:click={() => navigateUp()} />
			{/if}

			{#each files as item}
				{#if item.type === 'directory'}
					<SideNavLink
						icon={Folder}
						text={item.name}
						onclick={() => navigateToDirectory(item.path)}
					/>
				{:else}
					<SideNavLink
						icon={Document}
						text={item.name}
						onclick={() => handleFileClick(item.path)}
					/>
				{/if}
			{/each}
		{/if}
	</SideNavItems>
</SideNav>

<Content>
	<Grid>
		<Row>
			<Column>
				{#if fileLoadingErrorMsg}
					<div class="error">
						<h1 class="bx--data-table-header__title">No log traces loaded</h1>
						<p class="bx--data-table-header__description">
							Error: {fileLoadingErrorMsg}
						</p>
					</div>
				{:else}
					{@render children()}
				{/if}
			</Column>
		</Row>
	</Grid>
</Content>

<style>
    :global(.bx--side-nav__items:hover) {
        cursor: pointer;
    }

    :global(div) {
        margin-top: var(--cds-spacing-05);
    }

    :global(.bx--content) {
        padding-left: 0;
        padding-right: 0;
    }

    :global(.bx--header) {
        background-color: black;
    }

    :global(.bx--tree .bx--tree-node--active > .bx--tree-node__label::before) {
        background-color: rgb(127, 82, 255);
    }
</style>
