import {useEffect, useState} from "react";
import {useNavigate, useParams} from "react-router-dom";
import type {Project} from "../types/project";
import type {ProjectImage, ProjectImageElem} from "../types/image";
import {getProject, updateProject} from "../api/projects.ts";
import {getTagsForProject} from "../api/tags.ts";
import {createImageForProject, deleteImageForProject, getImagesForProject, updateImageForProject} from "../api/images.ts";
import {HttpStatusCode} from "axios";
import {navToErrorPage} from "./ErrorPage.tsx";
import {LoadingOverlay} from "../components/business/LoadingOverlay.tsx";
import {ProjectNameInput} from "../components/business/ProjectNameInput.tsx";
import {ProjectDescriptionInput} from "../components/business/ProjectDescriptionInput.tsx";
import {ProjectVisibilityRadio} from "../components/business/ProjectVisibilityRadio.tsx";
import {ProjectTagInput} from "../components/business/ProjectTagInput.tsx";
import {ImageEditor} from "../components/business/ImageEditor.tsx";
import {Background, BackgroundBorder, EdgePadding} from "../components/ui/Background.tsx";
import {H1} from "../components/ui/Text.tsx";
import {PrimaryButton} from "../components/ui/Button.tsx";

export function EditProjectPage() {
    const {username} = useParams<{ username: string }>()
    const {projectName} = useParams<{ projectName: string }>()
    const [project, setProject] = useState<Project>(null)
    const [name, setName] = useState<string>("")
    const [description, setDescription] = useState<string>("")
    const [visibility, setVisibility] =  useState<'public' | 'private'>('private')
    const [tags, setTags] = useState<string[]>([])
    const [images, setImages] = useState<ProjectImageElem[]>([])
    const [deletedImages, setDeletedImages] = useState<ProjectImage[]>([])
    const [isLoadingProject, setIsLoadingProject] = useState(true);
    const [isLoadingTags, setIsLoadingTags] = useState(true);
    const [isLoadingImages, setIsLoadingImages] = useState(true);
    const [error, setError] = useState<string | null>(null)
    const nav = useNavigate();

    useEffect(() => {
        if (!projectName) {
            setError("Null project name");
            navToErrorPage(nav, error);
            return;
        }
        if (!username) {
            setError("Null username");
            navToErrorPage(nav, error);
            return;
        }

        getProject(username, projectName)
            .then((retrievedProject) => {
                setProject(retrievedProject)
                setName(retrievedProject.projectName)
                setDescription(retrievedProject.description)
                setVisibility(retrievedProject.visibility.toLowerCase() as ("public" | "private"))
                setIsLoadingProject(false);
            })
            .catch(err => {
                navToErrorPage(nav, err);
            })

        getTagsForProject(username, projectName)
            .then((retrievedTags) => {
                let tagStrings: string[] = []
                for (const tag of retrievedTags) {
                    tagStrings.push(tag.tagName)
                }
                setTags(tagStrings)
                setIsLoadingTags(false);
            })
            .catch(err => {
                navToErrorPage(nav, err);
            })

        getImagesForProject(username, projectName)
            .then((retrievedImages) => {
                setImages(retrievedImages)
                setIsLoadingImages(false);
            })
            .catch(err => {
                navToErrorPage(nav, err);
            })

    }, [username, projectName]);

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
        if (!username) {
            setError("Null username");
            navToErrorPage(nav, error);
            return;
        }

        try {
            await updateProject(username, project.id, name, description, visibility, tags)

            // delete images
            for (const img of deletedImages) {
                const res = await deleteImageForProject(username, name, img.id)
                if (res.status !== HttpStatusCode.NoContent) {
                    setError(`${res.statusText}: Failed to delete image with id=${img.id}`)
                    navToErrorPage(nav, error);
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
                    const newImage: ProjectImage = await createImageForProject(username, name, image.url, image.caption, image.dateTime)
                    positionToImageMap.set(i, newImage)
                }
            }

            // update all images using the map
            for (let i = 0; i < images.length; i++) {
                const image: ProjectImage = positionToImageMap.get(i)
                await updateImageForProject(username, name, image.id, i, image.url, image.caption, image.dateTime)
            }

            nav("/projects/" + name)
        }
        catch (err) {
            // todo handle unavailable names without nav
            navToErrorPage(nav, err);
        }
    };

    const isLoading = isLoadingProject || isLoadingTags || isLoadingImages;

    return (
        <Background>
            <BackgroundBorder>
                <EdgePadding>
                    <H1 content="Edit Project" />
                    <form onSubmit={handleSubmit}>
                        {!isLoadingProject && <ProjectNameInput name={name} setName={setName} />}
                        {!isLoadingProject && <ProjectDescriptionInput description={description} setDescription={setDescription} />}
                        {!isLoadingProject && <ProjectVisibilityRadio visibility={visibility} setVisibility={setVisibility} />}
                        {!isLoadingTags && <ProjectTagInput tags={tags} setTags={setTags} />}
                        {!isLoadingImages && <ImageEditor projectName={projectName} images={images} setImages={setImages} addDeletedImage={addDeletedImage}/>}
                        <PrimaryButton type="submit" text="Save changes" disabled={isLoading && (name.trim() === "" || visibility.trim() === "")} />
                    </form>
                    {isLoading && <LoadingOverlay/>}
                </EdgePadding>
            </BackgroundBorder>
        </Background>
    );
}
