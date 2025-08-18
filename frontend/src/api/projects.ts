import api from "./axios"
import type {Project} from "../types/project"
import type {Tag} from "../types/tag";

export async function getAllProjects(username: string): Promise<Project[]> {
    return api.get<Project[]>("/" + username + "/projects")
        .then(res => res.data as Project[]);
}

export async function getAllPublicProjects(username: string): Promise<Project[]> {
    const params = new URLSearchParams({ visibility: 'public' });
    return api.get<Project[]>("/" + username + `/projects?${params.toString()}`)
        .then(res => res.data as Project[]);
}

export async function getAllProjectsWithTags(username: string, tags: Tag[]): Promise<Project[]> {
    if (tags.length == 0) {
        return getAllProjects(username);
    }
    let tagParams = "";
    for (const tag of tags) {
        tagParams += tag.tagName + ","
    }
    const params = new URLSearchParams({ tags: tagParams.substring(0, tagParams.length - 1) });

    return api.get<Project[]>("/" + username + `/projects?${params.toString()}`)
        .then(res => res.data as Project[]);
}

export async function getProject(username: string, projectName: string): Promise<Project> {
    return api.get<Project>("/" + username + `/projects/${projectName}`)
        .then(res => res.data as Project);
}

export async function createProject(username: string, name: string, description: string, visibility: string, tags: string[] ) : Promise<Project> {
    const body = {
        projectName: name,
        description: description,
        visibility: visibility.toUpperCase(),
        tagStrings: tags
    }
    return api.post("/" + username + "/projects", body)
        .then(res => res.data as Project)
}

export async function updateProject(username: string, id: number, name: string, description: string, visibility: string, tags: string[] ) : Promise<Project> {
    const body = {
        id: id,
        projectName: name,
        description: description,
        visibility: visibility.toUpperCase(),
        tagStrings: tags
    }
    return api.put("/" + username + "/projects", body)
        .then(res => res.data as Project)
}

export async function deleteProject(username: string, projectName: string) {
    return api.delete<void>("/" + username + `/projects/${projectName}`)
}
