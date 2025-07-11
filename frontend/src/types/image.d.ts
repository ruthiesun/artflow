export type ProjectImagePrePersist = {
    position: number
    caption: string
    dateTime: Date | null
    url: string
    projectName: string
}

export type ProjectImage = ProjectImagePrePersist & {
    id: number
}

export type ProjectImageElem = ProjectImage | ProjectImagePrePersist
