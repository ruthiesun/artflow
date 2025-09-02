import {Background, BackgroundBorder, EdgePadding} from "../components/ui/Background.tsx";
import { Text } from "../components/ui/Text.tsx";

export function RegisterSuccessPage() {
    return (
        <Background>
            <BackgroundBorder>
                <EdgePadding>
                    <Text content="Check your email to verify your account." />
                </EdgePadding>
            </BackgroundBorder>
        </Background>
    );
}
