import {useEffect, useState} from "react";
import type {ProjectImage} from "../types/image";
import {getAllImagesForProject} from "../api/images.ts";

type ImageCarouselProps = {
    projectName: string;
};

export function ImageCarousel({ projectName }: ImageCarouselProps) {
    const [images, setImages] = useState<ProjectImage[]>([])

    useEffect(() => {
        getAllImagesForProject(projectName)
            .then((retrievedImages) =>
                setImages(retrievedImages)
            );}, []);

    return (
        <div>
            {images.map((src, index) => (
                <div key={index}>
                    <img

                        src={src.url}
                        alt={`Image ${index + 1}`}
                        className="h-40 rounded-xl shadow-md inline-block"
                    />
                    {src.dateTime == null ? (
                        <p>Date: unknown</p>
                    ) : (
                        <p>Date: {src.dateTime}</p>
                    )}
                    <p>{src.caption}
                    </p>
                    <p>
                        position: {src.position}
                    </p>
                </div>
            ))}
        </div>
    )
}

export function ImageCarouselPreview({ projectName }: ImageCarouselProps) {
    const [images, setImages] = useState<ProjectImage[]>([])

    useEffect(() => {
        getAllImagesForProject(projectName)
            .then((retrievedImages) =>
                setImages(retrievedImages)
            );}, []);

    return (
        <div className="overflow-x-auto whitespace-nowrap space-x-4 flex p-2">
            {images.map((src, index) => (
                <img
                    key={index}
                    src={src.url}
                    alt={`Image ${index + 1}`}
                    className="h-40 rounded-xl shadow-md inline-block"
                />
            ))}
        </div>
    )
}
