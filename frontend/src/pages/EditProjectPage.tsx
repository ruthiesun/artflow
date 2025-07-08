import {useEffect, useState} from "react";
import {Link, useNavigate, useParams} from "react-router-dom";
import type {Project} from "../types/project";
import {createProject, getProject, updateProject} from "../api/projects.ts";
import {getAllTagsForProject} from "../api/tags.ts";
import {ProjectNameInput} from "../components/ProjectNameInput.tsx";
import {ProjectDescriptionInput} from "../components/ProjectDescriptionInput.tsx";
import {ProjectVisibilityRadio} from "../components/ProjectVisibilityRadio.tsx";
import {ProjectTagInput} from "../components/ProjectTagInput.tsx";
import type {ProjectImage, ProjectImageElem, ProjectImagePrePersist} from "../types/image";
import {ImageEditor} from "../components/ImageEditor.tsx";
import {createImage, deleteImage, getAllImagesForProject, updateImage} from "../api/images.ts";
import {HttpStatusCode} from "axios";



export function EditProjectPage() {
    const { projectName } = useParams<{ projectName: string }>()
    const [project, setProject] = useState<Project>(null)
    const [error, setError] = useState<string | null>(null);
    const nav = useNavigate();
    const [name, setName] = useState<string>("")
    const [description, setDescription] = useState<string>("")
    const [visibility, setVisibility] =  useState<'public' | 'private'>('private');
    const [tags, setTags] = useState<string[]>([])
    const [loading, setLoading] = useState(true);
    const [images, setImages] = useState<ProjectImageElem[]>([])
    const [deletedImages, setDeletedImages] = useState<ProjectImage[]>([])

    if (projectName == null) {
        setError("Null project name")
        return <div>{error}</div>;
    }

    useEffect(() => {
        getProject(projectName)
            .then((retrievedProject) => {
                setProject(retrievedProject)
                setName(retrievedProject.projectName)
                setDescription(retrievedProject.description)
                setVisibility(retrievedProject.visibility.toLowerCase() as ('public' | 'private'))
                setLoading(false)
                console.log(project)
            });}, []);

    useEffect(() => {
        getAllTagsForProject(projectName)
            .then((retrievedTags) => {
                let tagStrings: string[] = []
                for (const tag of retrievedTags) {
                    tagStrings.push(tag.tagName)
                }
                setTags(tagStrings)
                console.log(tags)
            });}, []);

    useEffect(() => {
        getAllImagesForProject(projectName)
            .then((retrievedImages) => {
                setImages(retrievedImages)
            });}, []);

    if (loading) return <div>Loading...</div>;
    if (error) return <div>{error}</div>;
    if (!project) return <div>No project found</div>;


    const addDeletedImage = ((image: ProjectImage) => {
        setDeletedImages([...deletedImages, image])
    })

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null);

        if (!name || !visibility) {
            setError("Please fill in all fields.");
            return;
        }

        updateProject(project.id, name, description, visibility, tags)
            .then((updatedProject: Project) => {
                console.log(updatedProject)
                nav('/projects/' + updatedProject.projectName)
            }).catch((err) => {
            console.log(err)
        });

        // delete images
        for (const img of deletedImages) {
            await deleteImage(projectName, img.id)
                .then((res) => {
                    if (res.status !== HttpStatusCode.NoContent) {
                        console.error(`Failed to delete image ${img.id}:`, res.statusText);
                    }
                })
        }
        //
        // const results = await Promise.allSettled(
        //     deletedImages.map((img) => deleteImage(projectName, img.id))
        // );
        //
        // // Log or act on failures
        // results.forEach((result, i) => {
        //     if (result.status === 'rejected') {
        //         console.error(`Failed to delete image ${deletedImages[i].id}:`, result.reason);
        //     }
        // });

        // init position to projectimage map (should end up being a 1 to 1 map)
        const positionToImageMap = new Map<number, ProjectImage>();
        // as needed, create new images and update map
        const imageCreatePromises: Promise<[number, ProjectImage]>[] = [];

        for (let i = 0; i < images.length; i++) {
            const image = images[i];

            // If the image is already complete, just map it
            if ("id" in image) {
                console.log("adding image with prev pos=" + image.position + " to queue in new position " + i)
                positionToImageMap.set(i, image);
            } else {
                await createImage(projectName, image.url, image.caption, image.dateTime).then(newImage => [i, newImage] as [number, ProjectImage]);
                // const promise = createImage(projectName, image.url, image.caption, image.dateTime).then(newImage => [i, newImage] as [number, ProjectImage]);
                // imageCreatePromises.push(promise);
            }
        }
        // fulfill all promises, filling in the map
        const fulfilled = await Promise.all(imageCreatePromises);
        for (const [index, newImage] of fulfilled) {
            positionToImageMap.set(index, newImage);
        }
        // update all images
        for (let i = 0; i < images.length; i++) {
            const image: ProjectImage = positionToImageMap.get(i)
            await updateImage(projectName, image.id, i, image.url, image.caption, image.dateTime)
        }
    };

    return (
        <div>
                <form onSubmit={handleSubmit}>
                    <ProjectNameInput name={name} setName={setName} />
                    <ProjectDescriptionInput description={description} setDescription={setDescription} />
                    <ProjectVisibilityRadio visibility={visibility} setVisibility={setVisibility} />
                    <ProjectTagInput tags={tags} setTags={setTags} />

                    <button type="submit">
                        Save changes
                    </button>
                </form>
            <ImageEditor projectName={projectName} images={images} setImages={setImages} addDeletedImage={addDeletedImage}/>
        </div>
    )
}
