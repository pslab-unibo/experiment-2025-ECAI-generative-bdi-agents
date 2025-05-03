<script lang="ts">
	import 'highlight.js/styles/github-dark.css'
	import type { ChatMessage } from '$lib/types'
	import { loadChatData } from '$lib/fileLoader'
	import { onMount } from 'svelte';
	import SvelteMarkdown from 'svelte-marked';
	import CodeRenderer from './CodeRenderer.svelte';
	import 'highlight.js/styles/github.css';

	const {
		jsonPath = null
	} = $props<{
		jsonPath: string | null
	}>()

	let messages = $state<ChatMessage[]>([])
	let isLoading = $state<Boolean>(true)
	let error = $state<string | null>(null)

	function handleCopyClick(e: MouseEvent): void {
		const target = e.target as HTMLElement
		if (target.classList.contains('copy-button')) {
			const button = target as HTMLButtonElement
			const code = decodeURIComponent(button.dataset.code || '')
			navigator.clipboard
				.writeText(code)
				.then(() => {
					const originalText = button.textContent
					button.textContent = 'Copied!'
					setTimeout(() => {
						button.textContent = originalText
					}, 2000)
				})
				.catch((err) => console.error('Failed to copy code:', err))
		}
	}

	async function fetchChatData() {
		isLoading = true;
		messages = (await loadChatData(jsonPath)).messages
		// error = result.error;
		isLoading = false;
	}

	$effect(()  => {
		if (jsonPath !== null) {
			fetchChatData()
		}
	})

	onMount(() => {
		// Add event listener for copy buttons
		document.addEventListener('click', handleCopyClick)

		// Cleanup event listener on component destruction
		return () => {
			document.removeEventListener('click', handleCopyClick)
		}
	})
</script>

<main class="bx--data-table-container">
	{#if isLoading}
		<div class="loading">
			<div class="spinner"></div>
			<p>Loading chat...</p>
		</div>
	{:else if error}
		<div class="error">
			<h1 class="bx--data-table-header__title">No log traces loaded</h1>
			<p class="bx--data-table-header__description">
				Error: {error}
			</p>
		</div>
	{:else if messages.length === 0}
		<div class="empty-state">
			<p>No messages found in the chat.</p>
		</div>
	{:else}
		<div class="messages">
			{#each messages as message}
				<div class="message {message.role}">
					<span class="role-label">{message.role}</span>
					<SvelteMarkdown source={message.content} renderers={{code: CodeRenderer}} />
				</div>
			{/each}
		</div>
	{/if}
</main>

<style>
		.system {
				background-color: rgba(228, 174, 174, 0.23);
		}
		.user {
			background-color: #b9c9ff;
		}
		.assistant {
				background-color: rgba(159, 205, 137, 0.27);
		}

    .role-label {
        position: absolute;
        top: 0.8rem;
        left: 0.8rem;
        font-size: 0.8rem;
        font-weight: bold;
        text-transform: capitalize;
        padding: 0.2rem 0.5rem;
        border-radius: 0.25rem;
        background-color: rgba(0, 0, 0, 0.1);
    }

    .user .role-label {
        background-color: #a8baff;
        color: #00236f;
    }

    .assistant .role-label {
        background-color: rgba(119, 178, 85, 0.4);
        color: #1e5208;
    }

    .system .role-label {
        background-color: rgba(205, 123, 123, 0.4);
        color: #6a0505;
    }


    .messages {
        display: flex;
        flex-direction: column;
        gap: 1rem;
        padding: 2rem 6rem;
    }

    .message {
				border: 1px solid #000000;
        padding: 2rem 3rem;
        border-radius: 0.8rem;
        animation: fadeIn 0.3s ease-in-out;
        position: relative;
    }
    :global(pre) {
				background: transparent;
        font-size: 1.1rem;
				line-height: 1.33;
    }
    :global(code) {
        font-size: 0.875rem;
        padding: 0.2rem 0.4rem;
        border-radius: 0.25rem;
    }
    :global(.messages h1) {
        margin-top: 2rem;
        margin-bottom: 1rem;
        font-weight: 400;
        font-size: 2.3rem;
        padding-top: 1rem;
        padding-bottom: 1rem;
    }

    :global(.messages h2) {
        margin-top: 1.8rem;
        margin-bottom: 0.8rem;
        font-size: 2rem;
        padding-top: 0.5rem;
        padding-bottom: 0.5rem;
    }

    :global(.messages h3) {
        margin-top: 1.5rem;
        margin-bottom: 0.7rem;
        font-size: 1.5rem;
        padding-top: 0.5rem;
        padding-bottom: 0.5rem;
    }

    :global(.messages h4, h5, h6) {
        margin-top: 1.2rem;
        margin-bottom: 0.6rem;
        padding-top: 0.5rem;
        padding-bottom: 0.5rem;
    }

    :global(h1 + h2),
    :global(h2 + h3),
    :global(h3 + h4),
    :global(h4 + h5),
    :global(h5 + h6) {
        margin-top: 0.7rem;
    }

    .messages :global(p) {
        margin-bottom: 1.2rem;
        font-size: 1.2rem;
        line-height: 1.2;
    }

    .messages :global(p + p) {
        margin-top: 0.8rem;
    }


    .messages :global(ul) {
        margin-top: 1rem;
        margin-bottom: 1.2rem;
        padding-top: 0.5rem;
        padding-bottom: 0.5rem;
        line-height: 1.5;
        list-style: disc;
        padding-left: 1.3rem;
    }


    :global(li) {
        margin-bottom: 0.5rem;
        font-size: 1.1rem;
    }

    :global(li:last-child) {
        margin-bottom: 0;
    }


    :global(li p) {
        margin-bottom: 0.5rem;
    }


    :global(p + ul) {
        margin-top: 0.7rem;
    }
    .loading {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        padding: 2rem;
    }

    .empty-state {
        text-align: center;
        padding: 2rem;
        color: #6b7280;
    }

    @keyframes fadeIn {
        from {
            opacity: 0;
            transform: translateY(10px);
        }
        to {
            opacity: 1;
            transform: translateY(0);
        }
    }
</style>