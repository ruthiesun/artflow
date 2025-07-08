type ProjectNameInputProps = {
    name: string;
    setName: ((newValue: string) => void)
};

export function ProjectNameInput({ name, setName }: ProjectNameInputProps) {
    return (
        <div>
            <label>Name</label>
            <input
                type="text"
                value={name}
                onChange={e => setName(e.target.value)}
            />
        </div>
    )
}
