import {useEffect, useState} from "react";
import type {ProjectImage} from "../types/image";
import {getImagesForProject} from "../api/images.ts";
import {Modal} from "./Modal.tsx";
import { navToErrorPage } from "../pages/ErrorPage.tsx";
import {useNavigate} from "react-router-dom";
import {H1, H3, Text, TimestampText} from "./Text.tsx";

type ImageDisplayProps = {
    image: ProjectImage
}

function ImageDisplaySmall({image}: ImageDisplayProps) {
    return (
        <img
        src={image.url}
        alt={`image in position: ${image.position}`}
        className="h-full rounded-sm inline-block object-contain shadow-sm"
    />)
}

function ImageDisplayLarge({image}: ImageDisplayProps) {
    return (
        <img
            src={image.url}
            alt={`image in position: ${image.position}`}
            className="h-auto max-h-[90vh] max-w-full rounded-lg object-contain shadow-sm"
        />)
}

type ImageDetailsProps = {
    image: ProjectImage;
    onClose: () => void
}

function ImageDetails({image, onClose}: ImageDetailsProps) {
    return (
        <Modal content={
            <div>
                <ImageDisplayLarge image={image} />
                {image.dateTime == null ? (
                    <p>Date: unknown</p>
                ) : (
                    <p>Date: {image.dateTime}</p>
                )}
                <p>{image.caption}</p>
            </div>
        } onClose={onClose} />
    )
}

type ImageCarouselProps = {
    projectName: string;
};

export function ImageCarousel({ projectName }: ImageCarouselProps) {
    const [images, setImages] = useState<ProjectImage[]>([])
    const [selectedImage, setSelectedImage] = useState<ProjectImage>(null)
    const [showImageDetailsModal, setShowImageDetailsModal] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const nav = useNavigate()

    useEffect(() => {
        getImagesForProject(projectName)
            .then((projectImages) =>
                setImages(projectImages)
            )
            .catch((err) => {
                navToErrorPage(nav, err);
            })
        }, []);

    const prepareToShowImageDetails = (img: ProjectImage) => {
        setSelectedImage(img);
        setShowImageDetailsModal(true);
    }

    return (
        <div>
            {images.map((image, index) => (
                <div key={index} className="flex-col flex">
                    <div className="cursor-pointer hover:opacity-50 h-max w-max max-w-full"
                        onClick={() => prepareToShowImageDetails(image)}>
                        <ImageDisplayLarge image={image} />
                        <div>
                        test
                        </div>
                    </div>
                    <div className="h-10 sm:h-12 md:h-14 lg:h-16">
                    </div>
                </div>
            ))}
            {selectedImage && showImageDetailsModal && <ImageDetails image={selectedImage} onClose={() => setShowImageDetailsModal(false)} />}
        </div>
    )
}

export function ImageCarouselPreview({ projectName, }: ImageCarouselProps) {
    const [images, setImages] = useState<ProjectImage[]>([])
    const [error, setError] = useState<string | null>(null);
    const nav = useNavigate()

    useEffect(() => {
        getImagesForProject(projectName)
            .then((projectImages) =>
                setImages(projectImages)
            )
            .catch((err) => {
                navToErrorPage(nav, err);
            })
    }, []);

    return (
        <div className="overflow-x-auto whitespace-nowrap
        flex-row flex
        space-x-1 sm:space-x-2 md:space-x-3 lg:space-x-4
        p-1 sm:p-2 md:p-3 lg:p-4
        scrollbar-thin scrollbar-thumb-gray-400 scrollbar-track-gray-100">
            {images.map((image, index) => (
                <div key={index} className="flex-shrink-0
                h-40 sm:h-48 md:h-56 lg:h-64">
                    <ImageDisplaySmall image={image} />
                </div>
            ))}
        </div>
    )
}
