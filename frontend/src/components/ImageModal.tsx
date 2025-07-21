import {useState} from "react";
import type {ProjectImageElem, ProjectImagePrePersist} from "../types/image";


type EditImageModalProps = {
    editingImage: ProjectImageElem,
    setImages: ((prev: ProjectImageElem[]) => void),
    images: ProjectImageElem[]
    onClose: (() => void)
}

export function EditImageModal({ editingImage, setImages, images, onClose }: EditImageModalProps) {
    const [url, setUrl] = useState<string>(editingImage.url);
    const [date, setDate] = useState<Date>(editingImage.dateTime);
    const [caption, setCaption] = useState<string>(editingImage.caption);
    const [error, setError] = useState<string | null>(null);

    const handleSubmit = ((e) => {
        e.preventDefault();

        if (!url) {
            setError("URL must be nonempty")
            return
        }

        const updatedImages = images.map((img) =>
            img.position === editingImage.position
                ? { ...img, url: url, dateTime: date, caption: caption }
                : img
        );
        setImages(updatedImages);
        onClose();
    })

    return (
        <div>
            <form onSubmit={handleSubmit}>
                {error && <p>{error}</p>}
                <UrlInput url={url} setUrl={setUrl}/>
                <CaptionInput caption={caption} setCaption={setCaption}/>
                <DateInput date={date} setDate={setDate}/>
                <button type='submit'>
                    Update
                </button>
            </form>
        </div>
    )
}

type AddImageModalProps = {
    projectName: string,
    setImages: ((prev: ProjectImageElem[]) => void),
    images: ProjectImageElem[]
    onClose: (() => void)
}

export function AddImageModal({projectName, setImages, images, onClose}: AddImageModalProps) {
    const [url, setUrl] = useState<string>("");
    const [date, setDate] = useState<Date>(new Date());
    const [caption, setCaption] = useState<string>("");
    const [error, setError] = useState<string | null>(null);

    const updateImages = (() => {
        if (!url) {
            setError("URL must be nonempty")
            return
        }
        const newImage: ProjectImagePrePersist = {position: images.length, caption: caption, dateTime: date, url: url, projectName: projectName}
        setImages([...images, newImage]);
        onClose()
    })

    return (
        <div>
                {error && <p>{error}</p>}
                <UrlInput url={url} setUrl={setUrl}/>
                <CaptionInput caption={caption} setCaption={setCaption}/>
                <DateInput date={date} setDate={setDate}/>
                <button type="button" onClick={updateImages}>
                    Create
                </button>
        </div>
    )
}

function UrlInput({ url, setUrl }) {
    return (
        <div>
            <label>Url</label>
            <input
                type="text"
                value={url}
                onChange={e => setUrl(e.target.value)}
            />
        </div>
    )
}

function CaptionInput({ caption, setCaption }) {
    return (
        <div>
            <label>Caption</label>
            <input
                type="text"
                value={caption}
                onChange={e => setCaption(e.target.value)}
            />
        </div>
    )
}

function DateInput({ date, setDate }) {
    return (
        <div>
            <label>Date</label>
            <input
                type="text"
                value={date}
                onChange={e => setDate(e.target.value)}
            />
        </div>
    )
}
