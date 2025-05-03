import type { ChatMessage, LogEntry } from '$lib/types'

/**
 * Loads file content from the API endpoint
 * @param filePath Path to the file to load
 * @returns Object with the loaded data, loading state, and any error
 */
export async function loadFileContent<T>(filePath: string | null): Promise<{
	data: T | null;
	error: string | null;
}> {
	if (!filePath) {
		return { data: null, error: null };
	}

	try {
		const response =
			await fetch(`/api/file-content?path=${encodeURIComponent(filePath)}`)

		if (!response.ok) {
			console.error(`Failed to load file data: ${response.status}`)
			return {
				data: null,
				error: `Failed to load file data: ${response.status}`
			};
		}

		const content = await response.json();
		return {
			data: content,
			error: null
		};
	} catch (err: any) {
		console.error('Error loading file data:', err);
		return {
			data: null,
			error: err.message
		};
	}
}

/**
 * Loads chat messages from a JSON file
 * @param jsonPath Path to the JSON file containing chat messages
 * @returns Object with the loaded messages (containing only role and content), loading state, and any error
 */
export async function loadChatData(jsonPath: string | null): Promise<{
	messages: ChatMessage[];
	error: string | null;
}> {
	const { data, error } = await loadFileContent<any>(jsonPath);

	const messages = data?.entries
		? data.entries
			.filter((entry: any) => entry.role && entry.content)
			.map((entry: any) => ({
				role: entry.role,
				content: entry.content
			}))
		: [];

	return {
		messages,
		error
	};
}
