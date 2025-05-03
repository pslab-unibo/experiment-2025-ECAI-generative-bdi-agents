export interface ChatMessage {
	role: 'user' | 'assistant' | 'system' | string
	content: string
}

export interface LogEntry {
	timestamp: number
	level: string
	threadName: string
	loggerName: string
	message: string
}

export interface FileItem {
	name: string
	type: 'file' | 'directory'
	path: string
}
