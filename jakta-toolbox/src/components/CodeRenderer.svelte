
<script lang="ts">
	import hljs from 'highlight.js';

	export let text = '';
	export let lang = '';

	let highlightedText = hljs.highlight(text, { language: lang || 'plaintext' }).value;
</script>

<pre class={`language-` + lang}>
	<code class="hljs">{@html highlightedText}</code>
</pre>