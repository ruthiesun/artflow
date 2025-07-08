import { useEffect, useState } from 'react'

import type { Project } from '../types/project'
import {getAllProjectsWithTags} from "../api/projects.ts";
import {ImageCarouselPreview} from "../components/ImageCarousel.tsx";
import {Link} from "react-router-dom";
import {getAllTags} from "../api/tags.ts";
import type {Tag} from "../types/tag";
import {useNavigate} from "react-router-dom";

export function HomePage() {
    const [projects, setProjects] = useState<Project[]>([])
    const [deselectedTags, setDeselectedTags] = useState<Tag[]>([])
    const [selectedTags, setSelectedTags] = useState<Tag[]>([])
    const nav = useNavigate()

    useEffect(() => {
        console.log("calling getAllTags")
        getAllTags()
            .then((retrievedTags) => {
                setSelectedTags(retrievedTags)
                console.log("getAllTags: " + retrievedTags)
            });}, []);

    useEffect(() => {
        console.log("calling getAllProjectsWithTags")
        getAllProjectsWithTags(selectedTags)
            .then((retrievedProjects) => {
                setProjects(retrievedProjects)
                console.log("getAllProjectsWithTags: " + retrievedProjects)
            });}, [selectedTags]);


    const selectTag = (tag: Tag) => {
        setSelectedTags(prev => [...prev, tag]);
        setDeselectedTags(prev => prev.filter(t => t.tagName !== tag.tagName));
    };

    const deselectTag = (tag: Tag) => {
        setDeselectedTags(prev => [...prev, tag]);
        setSelectedTags(prev => prev.filter(t => t.tagName !== tag.tagName));
    };

    return (
        <div>
            <h1>My Projects</h1>
            <div>
                <button
                    type={"button"}
                    onClick={() => nav("new")}
                >
                    New project
                </button>
            </div>
            <div>
                {selectedTags.map((tag) => (
                    <div key={tag.tagName}>
                        <button
                            type="button"
                            className="bg-blue-500"
                            onClick={() => deselectTag(tag)}
                        >
                            {tag.tagName}
                        </button>
                    </div>
                ))}
                {deselectedTags.map((tag) => (
                    <div key={tag.tagName}>
                        <button
                            type="button"
                            onClick={() => selectTag(tag)}
                        >
                            {tag.tagName}
                        </button>
                    </div>
                ))}
            </div>
            <div>
                {projects.map((project) => (
                    <div key={project.projectName}>
                        <h2>
                            <Link to={project.projectName}>
                                {project.projectName}
                            </Link>
                        </h2>
                        <ImageCarouselPreview projectName={project.projectName} />
                    </div>
                ))}
            </div>
        </div>
    )
}
