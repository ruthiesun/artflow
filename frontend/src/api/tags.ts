import type {ProjectTag, Tag} from "../types/tag.d.ts";
import api from "./axios.ts";

export async function getTagsForUser(username: string): Promise<Tag[]> {
    return api.get<ProjectTag[]>("/" + username + '/tags')
        .then(res => res.data as Tag[]);
}

export async function getTagsForProject(username: string, projectName: string): Promise<ProjectTag[]> {
    return api.get<ProjectTag[]>("/" + username + `/projects/${projectName}/tags`)
        .then(res => res.data as ProjectTag[]);
}
