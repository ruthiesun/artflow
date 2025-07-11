import {useEffect, useState} from "react";
import type {ProjectImage} from "../types/image";
import {getImagesForProject} from "../api/images.ts";

type ImageCarouselProps = {
    projectName: string;
};

export function ImageCarousel({ projectName }: ImageCarouselProps) {
    const [images, setImages] = useState<ProjectImage[]>([])
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        getImagesForProject(projectName)
            .then((projectImages) =>
                setImages(projectImages)
            )
            .catch((err) => {
                setError(err)
            })
        }, []);

    if (error) return <div>{error}</div>;

    return (
        <div>
            {images.map((image, index) => (
                <div key={index}>
                    <img
                        src={image.url}
                        alt={`Image ${index + 1}`}
                        className="h-40 rounded-xl shadow-md inline-block"
                    />
                    {image.dateTime == null ? (
                        <p>Date: unknown</p>
                    ) : (
                        <p>Date: {image.dateTime}</p>
                    )}
                    <p>{image.caption}</p>
                    <p>position: {image.position}</p>
                </div>
            ))}
        </div>
    )
}

export function ImageCarouselPreview({ projectName }: ImageCarouselProps) {
    const [images, setImages] = useState<ProjectImage[]>([])
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        getImagesForProject(projectName)
            .then((projectImages) =>
                setImages(projectImages)
            )
            .catch((err) => {
                setError(err)
            })
    }, []);

    if (error) return <div>{error}</div>;

    return (
        <div className="overflow-x-auto whitespace-nowrap space-x-4 flex p-2">
            {images.map((image, index) => (
                <img
                    key={index}
                    src={image.url}
                    alt={`Image ${index + 1}`}
                    className="h-40 rounded-xl shadow-md inline-block"
                />
            ))}
        </div>
    )
}
