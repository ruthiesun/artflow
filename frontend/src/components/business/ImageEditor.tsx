import { useState } from "react";
import { CSS } from '@dnd-kit/utilities';
import {
    arrayMove,
    SortableContext,
    useSortable,
    rectSortingStrategy
} from "@dnd-kit/sortable";
import { closestCenter, DndContext, PointerSensor, useSensor, useSensors } from '@dnd-kit/core';
import type { ProjectImage, ProjectImageElem } from "../../types/image";
import { AddImageModal, EditImageModal } from "./ImageModal.tsx";
import { SecondaryButton } from "../ui/Button.tsx";
import { labelClass } from "../ui/Input.tsx";

type ImageEditorProps = {
    projectName: string
    images: ProjectImageElem[];
    setImages: (value: ProjectImageElem[] | ((prev: ProjectImageElem[]) => ProjectImageElem[])) => void;
    addDeletedImage: ((imageToDelete: ProjectImage) => void)
};

export function ImageEditor({ projectName, images, setImages, addDeletedImage }: ImageEditorProps) {
    const sensors = useSensors(useSensor(PointerSensor));
    const [showAddImageModal, setShowAddImageModal] = useState(false);
    const [showEditImageModal, setShowEditImageModal] = useState(false);
    const [editingImage, setEditingImage] = useState<ProjectImageElem | null>(null);

    const prepareToEdit = ((image: ProjectImageElem) => {
        setEditingImage(image)
        setShowEditImageModal(true)
    })

    const prepareToDelete = ((image: ProjectImageElem) => {
        if ("id" in image) {
            addDeletedImage(image as ProjectImage)
        }
        const updatedImages: ProjectImageElem[] = []
        for (const currImage of images) {
            if (image.position !== currImage.position) {
                updatedImages.push(currImage)
            }
        }
        setImages(updatedImages)
    })

    return (
        <div>
            <label className={labelClass}>Images</label>
            <div className="rounded-lg bg-white w-full flex justify-center items-center sm:justify-start">
                <DndContext
                    collisionDetection={closestCenter}
                    sensors={sensors}
                    onDragEnd={(event) => {
                        const { active, over } = event;
                        if (over && active.id !== over.id) {
                            const oldIndex = images.findIndex((img) => img.position === active.id);
                            const newIndex = images.findIndex((img) => img.position === over.id);
                            setImages(prev => arrayMove(prev, oldIndex, newIndex));
                        }
                    }}
                >
                    <SortableContext items={images.map(image => image.position)} strategy={rectSortingStrategy}>
                        <div className="flex flex-wrap w-min sm:w-auto">
                            {images.map((image) => (
                                <SortableImage key={image.position} image={image} onEdit={prepareToEdit} onDelete={prepareToDelete} />
                            ))}
                        </div>
                    </SortableContext>
                </DndContext>
            </div>
            <div className="mt-2">
                <SecondaryButton type="button" text="Add new image" onClick={() => setShowAddImageModal(true)} />
            </div>

            {showAddImageModal &&
                <AddImageModal projectName={projectName} setImages={setImages} images={images} onClose={(() => setShowAddImageModal(false))} />
            }
            {showEditImageModal && editingImage != null &&
                <EditImageModal editingImage={editingImage} setImages={setImages} images={images} onClose={(() => setShowEditImageModal(false))} />
            }
        </div>
    )
}

type SortableImageProps = {
    image: ProjectImageElem;
    onEdit: (image: ProjectImageElem) => void;
    onDelete: (image: ProjectImageElem) => void;
};

function SortableImage({ image, onEdit, onDelete }: SortableImageProps) {
    const [isBeingMoved, setIsBeingMoved] = useState(false);

    const {
        attributes,
        listeners,
        setNodeRef,
        transform,
        transition,
    } = useSortable({ id: image.position });

    const style = {
        transform: CSS.Transform.toString(transform),
        transition,
    };

    const imgOpacity = isBeingMoved ? "opacity-75" : "opacity-100"

    return (
        <div
            ref={setNodeRef}
            style={style}
            {...attributes}

            className="w-48 h-48 ml-2 mr-2 mt-2 mb-4 shadow-sm rounded-sm overflow-hidden relative focus:z-10 group"
        >
            <img src={image.url} alt="A project image" className={`object-cover w-full h-full z-0 ${imgOpacity} transition`} />

            <div className="absolute top-1 left-1 z-1 flex flex-row">
                <ImageButton iconSrc="/icons/edit.svg" alt="Edit" className="cursor-pointer"
                    onClick={(e) => {
                        e.stopPropagation(); // prevent drag handle click from triggering modal
                        onEdit(image);
                    }} />
                <ImageButton iconSrc="/icons/delete.svg" alt="Delete" className="cursor-pointer"
                    onClick={(e) => {
                        e.stopPropagation(); // prevent drag handle click from triggering modal
                        onDelete(image);
                    }} />
                <div
                    {...listeners}
                    onMouseDown={() => setIsBeingMoved(true)}
                    onMouseUp={() => setIsBeingMoved(false)}>
                    <ImageButton iconSrc="/icons/drag.svg" alt="Move" className="cursor-grab" />
                </div>
            </div>
        </div>
    );
}

type ImageButtonProps = {
    iconSrc: string;
    alt: string;
    className?: string;
    onClick?: (e: any) => void;
}

function ImageButton({ iconSrc, alt, className, onClick }: ImageButtonProps) {
    return (
        <div
            className={`${className} p-1 mr-1 w-max h-max bg-white rounded`}
            onClick={onClick}
        >
            <img src={iconSrc} alt={alt} className="w-4 h-4" />
        </div>
    )
}
