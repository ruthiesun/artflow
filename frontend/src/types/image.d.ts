export type ProjectImagePrePersist = {
    position: number
    caption: string
    dateTime: string
    url: string
    projectName: string
}

export type ProjectImage = ProjectImagePrePersist & {
    id: number
}

export type ProjectImageElem = ProjectImage | ProjectImagePrePersist
