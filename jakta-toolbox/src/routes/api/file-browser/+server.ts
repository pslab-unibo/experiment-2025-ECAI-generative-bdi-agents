import { json, type RequestHandler } from '@sveltejs/kit'
import fs from 'fs/promises'
import path from 'path'
import type { FileItem } from '$lib/types'
import { PUBLIC_BASE_PATH } from '$env/static/public'

export const GET: RequestHandler = async ({ url }) => {
	const requestedPath = url.searchParams.get('path') || PUBLIC_BASE_PATH
	console.log('Requested path:', requestedPath)

	try {
		const items = await fs.readdir(requestedPath, { withFileTypes: true })

		const filesList: FileItem[] = items.map((item) => {
			const itemPath = path.join(requestedPath, item.name)
			return {
				name: item.name,
				type: item.isDirectory() ? 'directory' : 'file',
				path: itemPath
			}
		})

		// Sort directories first, then files
		filesList.sort((a, b) => {
			if (a.type === b.type) {
				return a.name.localeCompare(b.name)
			}
			return a.type === 'directory' ? -1 : 1
		})

		return json(filesList)
	} catch (error) {
		console.error('Error reading directory:', error)
		return new Response(JSON.stringify({ error: 'Failed to read directory' }), {
			status: 500,
			headers: { 'Content-Type': 'application/json' }
		})
	}
}
