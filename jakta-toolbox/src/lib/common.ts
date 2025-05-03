export function getFilenameWithoutExtension(filePath: string): string {
	if (!filePath) return ''
	const filename = filePath.split(/[\\/]/).pop() || ''
	return filename.replace(/\.[^/.]+$/, '')
}
