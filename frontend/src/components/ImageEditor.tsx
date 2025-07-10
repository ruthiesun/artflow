import {useState} from "react";
import type {ProjectImage, ProjectImageElem} from "../types/image";
import {CSS} from '@dnd-kit/utilities';
import {
    arrayMove,
    SortableContext,
    useSortable,
    verticalListSortingStrategy
} from "@dnd-kit/sortable";
import {closestCenter, DndContext, PointerSensor, useSensor, useSensors} from '@dnd-kit/core';
import {AddImageModal, EditImageModal} from "./ImageModal.tsx";
import type {Project} from "../types/project";

type ImageEditorProps = {
    projectName: string
    images: ProjectImageElem[];
    setImages: ((newImages: ProjectImageElem[]) => void)
    addDeletedImage: ((imageToDelete: ProjectImage) => void)
};

type SortableImageProps = {
    image: ProjectImageElem;
    onClick: (image: ProjectImageElem) => void;
};

export function ImageEditor({projectName, images, setImages, addDeletedImage}: ImageEditorProps) {
    const sensors = useSensors(useSensor(PointerSensor));
    const [showAddImageModal, setShowAddImageModal] = useState(false);
    const [showEditImageModal, setShowEditImageModal] = useState(false);
    const [editingImage, setEditingImage] = useState<ProjectImageElem | null>(null);

    const prepareToEdit = ((image: ProjectImageElem) => {
        console.log("edit")
        setEditingImage(image)
        setShowEditImageModal(true)
    })

    const prepareToDelete = ((image: ProjectImageElem) => {
        if ("id" in image) {
            addDeletedImage(image as ProjectImage)
        }
        const newImages: ProjectImageElem[] = []
        for (const img of images) {
            if (image.position !== img.position) {
                newImages.push(img)
            }
        }
        setImages(newImages)
    })

    return (
        <div>
            <div>
                <DndContext
                    collisionDetection={closestCenter}
                    sensors={sensors}
                    onDragEnd={(event) => {
                        const { active, over } = event;
                        if (over && active.id !== over.id) {
                            const oldIndex = images.findIndex((img) => img.position === active.id);
                            const newIndex = images.findIndex((img) => img.position === over.id);
                            setImages(((imgs) => arrayMove(imgs, oldIndex, newIndex)) as ProjectImageElem[]);
                        }
                    }}
                >
                    <SortableContext items={images.map(image => image.position)} strategy={verticalListSortingStrategy}>
                        <div className="flex flex-wrap">
                            {images.map((image) => (
                                <SortableImage key={image.position} image={image} onEdit={prepareToEdit} onDelete={prepareToDelete} />
                                // <SortableImage key={image.position} image={image} onClick={(()=>console.log("click"))} />
                            ))}
                        </div>
                    </SortableContext>
                </DndContext>
            </div>
            <button type='button' onClick={() => setShowAddImageModal(true)}>
                Add new image
            </button>
            {showAddImageModal && (
                <div>
                    <AddImageModal projectName={projectName} setImages={setImages} images={images} onClose={(() => setShowAddImageModal(false))}/>
                </div>
            )}
            {showEditImageModal && editingImage != null && (
                <div>
                    <EditImageModal editingImage={editingImage} setImages={setImages} images={images} onClose={(() => setShowEditImageModal(false))}/>
                </div>
            )}
        </div>
    )
}

function SortableImage({ image, onEdit, onDelete }: SortableImageProps) {
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

    return (
        <div
            ref={setNodeRef}
            style={style}
            {...attributes}

            className="w-32 h-32 m-2 border rounded overflow-hidden relative"
        >
            <img src={image.url} alt="thumb" className="object-cover w-full h-full" />

            {/* Optional: Add a visible drag handle icon */}
            <div
                className="absolute top-1 left-1 w-8 h-5 bg-gray-300 rounded cursor-pointer z-10"
                onClick={(e) => {
                    e.stopPropagation()// prevent drag handle click from triggering modal
                    onEdit(image)
                }}
            >
                <p>edit</p>
            </div>
            <div
                {...listeners}
                className="absolute top-1 left-10 w-8 h-5 bg-gray-300 rounded cursor-grab z-10"
            >
                <p>move</p>
            </div>
            <div
                className="absolute top-1 right-1 w-8 h-5 bg-gray-300 rounded cursor-pointer z-10"
                onClick={(e) => {
                    e.stopPropagation()// prevent drag handle click from triggering modal
                    onDelete(image)
                }}
            >
                <p>delete</p>
            </div>
        </div>
    );
}
