import {useEffect, useState} from "react";
import {useNavigate, useParams} from "react-router-dom";
import type {Project} from "../types/project";
import {getProject, updateProject} from "../api/projects.ts";
import {getTagsForProject} from "../api/tags.ts";
import {ProjectNameInput} from "../components/ProjectNameInput.tsx";
import {ProjectDescriptionInput} from "../components/ProjectDescriptionInput.tsx";
import {ProjectVisibilityRadio} from "../components/ProjectVisibilityRadio.tsx";
import {ProjectTagInput} from "../components/ProjectTagInput.tsx";
import type {ProjectImage, ProjectImageElem} from "../types/image";
import {ImageEditor} from "../components/ImageEditor.tsx";
import {createImageForProject, deleteImageForProject, getImagesForProject, updateImageForProject} from "../api/images.ts";
import {HttpStatusCode} from "axios";

export function EditProjectPage() {
    const {projectName} = useParams<{ projectName: string }>()
    const [project, setProject] = useState<Project>(null)
    const [name, setName] = useState<string>("")
    const [description, setDescription] = useState<string>("")
    const [visibility, setVisibility] =  useState<'public' | 'private'>('private')
    const [tags, setTags] = useState<string[]>([])
    const [images, setImages] = useState<ProjectImageElem[]>([])
    const [deletedImages, setDeletedImages] = useState<ProjectImage[]>([])
    const [error, setError] = useState<string | null>(null)
    const nav = useNavigate();

    if (projectName == null) {
        setError("Null project name")
    }

    useEffect(() => {
        if (!projectName) {
            setError("Null project name");
            return;
        }

        getProject(projectName)
            .then((retrievedProject) => {
                setProject(retrievedProject)
                setName(retrievedProject.projectName)
                setDescription(retrievedProject.description)
                setVisibility(retrievedProject.visibility.toLowerCase() as ("public" | "private"))
            })
            .catch(err => {
                setError(err.message)
            })

        getTagsForProject(projectName)
            .then((retrievedTags) => {
                let tagStrings: string[] = []
                for (const tag of retrievedTags) {
                    tagStrings.push(tag.tagName)
                }
                setTags(tagStrings)
            })
            .catch(err => {
                setError(err.message)
            })

        getImagesForProject(projectName)
            .then((retrievedImages) => {
                setImages(retrievedImages)
            })
            .catch(err => {
                setError(err.message)
            })

    }, [projectName]);

    const addDeletedImage = ((image: ProjectImage) => {
        setDeletedImages([...deletedImages, image])
    })

    const handleSubmit = async (e) => {
        e.preventDefault()
        setError(null)

        if (!name || !visibility) {
            setError("Please fill in all fields.")
            return;
        }

        try {
            await updateProject(project.id, name, description, visibility, tags)

            // delete images
            for (const img of deletedImages) {
                const res = await deleteImageForProject(name, img.id)
                if (res.status !== HttpStatusCode.NoContent) {
                    setError(`${res.statusText}: Failed to delete image with id=${img.id}`)
                }
            }

            // map each position to an image
            const positionToImageMap = new Map<number, ProjectImage>();

            for (let i = 0; i < images.length; i++) {
                const image = images[i];
                // If the image already exists, just map it to its new position
                if ("id" in image) {
                    positionToImageMap.set(i, image);
                } else {
                    const newImage: ProjectImage = await createImageForProject(name, image.url, image.caption, image.dateTime)
                    positionToImageMap.set(i, newImage)
                }
            }

            // update all images using the map
            for (let i = 0; i < images.length; i++) {
                const image: ProjectImage = positionToImageMap.get(i)
                await updateImageForProject(name, image.id, i, image.url, image.caption, image.dateTime)
            }

            nav("/projects/" + name)
        }
        catch (err) {
            setError(err.message)
        }
    };

    if (error || projectName == null) return <div>{error}</div>;
    if (!projectName || !project) return <div>Loading...</div>;

    return (
        <div>
            <form onSubmit={handleSubmit}>
                <ProjectNameInput name={name} setName={setName} />
                <ProjectDescriptionInput description={description} setDescription={setDescription} />
                <ProjectVisibilityRadio visibility={visibility} setVisibility={setVisibility} />
                <ProjectTagInput tags={tags} setTags={setTags} />
                <ImageEditor projectName={projectName} images={images} setImages={setImages} addDeletedImage={addDeletedImage}/>
                <button type="submit">
                    Save changes
                </button>
            </form>
        </div>
    )
}
