import type {ProjectTag, Tag} from "../types/tag.d.ts";
import api from "./axios.ts";

export async function getAllTags(): Promise<Tag[]> {
    return api.get<ProjectTag[]>('/tags')
        .then(res => res.data as Tag[]);
}

export async function getAllTagsForProject(projectName: string): Promise<ProjectTag[]> {
    return api.get<ProjectTag[]>('/projects/' + projectName + '/tags')
        .then(res => res.data as ProjectTag[]);
}
