import api from "./axios"
import type {Project} from "../types/project"
import type {Tag} from "../types/tag";

export async function getAllProjects(): Promise<Project[]> {
    return api.get<Project[]>('/projects')
        .then(res => res.data as Project[]);
}

export async function getAllPublicProjects(): Promise<Project[]> {
    const params = new URLSearchParams({ visibility: 'public' });
    return api.get<Project[]>(`/projects?${params.toString()}`)
        .then(res => res.data as Project[]);
}

export async function getAllProjectsWithTags(tags: Tag[]): Promise<Project[]> {
    if (tags.length == 0) {
        return getAllProjects();
    }
    let tagParams = "";
    for (const tag of tags) {
        tagParams += tag.tagName + ","
    }
    const params = new URLSearchParams({ tags: tagParams.substring(0, tagParams.length - 1) });

    return api.get<Project[]>(`/projects?${params.toString()}`)
        .then(res => res.data as Project[]);
}

export async function getProject(projectName: string): Promise<Project> {
    return api.get<Project>(`/projects/${projectName}`)
        .then(res => res.data as Project);
}

export async function createProject(name: string, description: string, visibility: string, tags: string[] ) : Promise<Project> {
    const body = {
        projectName: name,
        description: description,
        visibility: visibility.toUpperCase(),
        tagStrings: tags
    }
    return api.post("/projects", body)
        .then(res => res.data as Project)
}

export async function updateProject(id: number, name: string, description: string, visibility: string, tags: string[] ) : Promise<Project> {
    const body = {
        id: id,
        projectName: name,
        description: description,
        visibility: visibility.toUpperCase(),
        tagStrings: tags
    }
    return api.put("/projects", body)
        .then(res => res.data as Project)
}

export async function deleteProject(projectName: string) {
    return api.delete<void>(`/projects/${projectName}`)
}
