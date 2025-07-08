export interface ProjectImage {
    id: number
    position: number
    caption: string
    dateTime: Date | null
    url: string
    projectName: string
}

export interface ProjectImagePrePersist {
    position: number
    caption: string
    dateTime: Date | null
    url: string
    projectName: string
}

export type ProjectImageElem = ProjectImage | ProjectImagePrePersist
