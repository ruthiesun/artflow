import type {ProjectImage} from "../types/image.d.ts";
import api from "./axios.ts";

export async function getImagesForProject(projectName: string): Promise<ProjectImage[]> {
    return api.get<ProjectImage[]>(`/projects/${projectName}/images`)
        .then(res => res.data as ProjectImage[]);
}

export async function createImageForProject(projectName: string, url: string, caption: string, dateTime: Date | null) {
    const body = {
        projectName: projectName,
        url: url,
        caption: caption,
        dateTime: dateTime
    }

    return api.post<ProjectImage>(`/projects/${projectName}/images`, body)
        .then(res => res.data as ProjectImage);
}

export async function updateImageForProject(projectName: string, id: number, position: number, url: string, caption: string, dateTime: Date | null) {
    const body = {
        id: id,
        position: position,
        url: url,
        caption: caption,
        dateTime: dateTime
    }

    return api.put<ProjectImage>(`/projects/${projectName}/images`, body)
        .then(res => res.data as ProjectImage);
}

export async function deleteImageForProject(projectName: string, id: number) {
    return api.delete<void>(`/projects/${projectName}/images/${id}`)
}
