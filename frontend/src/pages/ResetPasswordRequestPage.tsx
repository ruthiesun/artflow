import {Background, BackgroundBorder, EdgePadding} from "../components/ui/Background.tsx";
import {Text} from "../components/ui/Text.tsx";

export function ResetPasswordRequestPage() {
    return (
        <Background>
            <BackgroundBorder>
                <EdgePadding>
                    <Text content="Check your email for a link to reset your password." />
                </EdgePadding>
            </BackgroundBorder>
        </Background>
    );
}