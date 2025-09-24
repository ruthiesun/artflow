import type {ProjectImage} from "../types/image.d.ts";
import api from "./axios.ts";

export async function getImagesForProject(username: string, projectName: string): Promise<ProjectImage[]> {
    return api.get<ProjectImage[]>("/" + username + `/projects/${projectName}/images`)
        .then(res => res.data as ProjectImage[]);
}

export async function createImageForProject(username: string, projectName: string, url: string, caption: string, dateTime: Date | null) {
    const body = {
        projectName: projectName,
        url: url,
        caption: caption,
        dateTime: dateTime
    }

    return api.post<ProjectImage>("/" + username + `/projects/${projectName}/images`, body)
        .then(res => res.data as ProjectImage);
}

export async function updateImageForProject(username: string, projectName: string, id: number, position: number, url: string, caption: string, dateTime: string) {
    const body = {
        id: id,
        position: position,
        url: url,
        caption: caption,
        dateTime: dateTime
    }

    return api.put<ProjectImage>("/" + username + `/projects/${projectName}/images`, body)
        .then(res => res.data as ProjectImage);
}

export async function deleteImageForProject(username: string, projectName: string, id: number) {
    return api.delete<void>("/" + username + `/projects/${projectName}/images/${id}`)
}
