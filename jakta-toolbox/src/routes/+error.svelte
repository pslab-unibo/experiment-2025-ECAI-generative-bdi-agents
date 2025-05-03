<script lang="ts">
	import { page } from '$app/state'
</script>

<div class="flex min-h-[50vh] items-center justify-center">
	<div class="flex flex-col items-center justify-center text-center p-4">
		<h1 class="text-4xl font-light mb-2">{page.status}</h1>
		<p class="text-lg font-light">{page.error?.message ?? "Something has gone wrong"}</p>

		<a href="/">
			Go back home
		</a>
	</div>
</div>

<style>
	a {
			border: 1px solid rgb(107, 59, 248);
			border-radius: 1rem;
			font-size: 1rem;
  		color: rgb(107, 59, 248);
			margin-top: 1rem;
			padding: 1rem 1rem;
	}
	a:hover {
			background-color: rgb(107, 59, 248);
			color: white;
	}
	h1 {
		font-size: 4.2rem;
		font-weight: 300;
		line-height: 1.199;
		letter-spacing: 0;
	}

	p {
		font-size: 1.4rem;
		font-weight: 300;
	}
</style>
