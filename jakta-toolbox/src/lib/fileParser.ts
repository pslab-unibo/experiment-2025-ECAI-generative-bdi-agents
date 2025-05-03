import type { LogEntry } from '$lib/types'
import { error } from '@sveltejs/kit'

export function parseLogFile(content: string): LogEntry[] {
	const lines = content.split('\n').filter((line) => line.trim() !== '')
	const entries: LogEntry[] = []

	if (lines.length === 0 && content.trim() !== '') {
		error(500, 'Invalid JSON Lines format: No valid JSON lines found.')
	}

	lines.forEach((line) => {
		const logEntry: LogEntry = JSON.parse(line)
		entries.push(logEntry)
	})

	return entries
}