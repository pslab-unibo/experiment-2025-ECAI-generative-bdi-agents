import { parseLogFile } from '$lib/fileParser'
import { error, type RequestHandler } from '@sveltejs/kit'
import fs from 'fs/promises'

export const GET: RequestHandler = async ({ url }) => {
	const filePath = url.searchParams.get('path') || './'
	console.log('Requested path:', filePath)

	if (!filePath) {
		throw error(400, 'File path is required')
	}

	try {
		const stats = await fs.stat(filePath)

		if (stats.isDirectory()) {
			error(400, 'Path is a directory, not a file')
		}

		// Avoid loading very large files
		if (stats.size > 5 * 1024 * 1024) {
			// 5MB limit
			error(413, 'File is too large to display')
		}

		const content = await fs.readFile(filePath, 'utf-8')

		const entries = parseLogFile(content)

		return new Response(
			JSON.stringify({
				entries: entries,
			}),
			{
				headers: {
					'Content-Type': 'application/json'
				}
			}
		)
	} catch (err: any) {
		console.error('Error reading file:', err)
		error(500, 'Failed to read file')
	}
}
