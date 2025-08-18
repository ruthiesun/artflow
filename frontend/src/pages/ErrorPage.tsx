import {useLocation, useNavigate} from 'react-router-dom';
import {AxiosError} from "axios";
import {H1, H3, Text} from "../components/ui/Text.tsx";
import {Background, BackgroundBorder, EdgePadding} from "../components/ui/Background.tsx";

type ErrorState = {
    status?: number;
    message?: string;
    details?: any;
};

export function ErrorPage() {
    const location = useLocation();
    const navigate = useNavigate();
    const state = location.state as ErrorState;

    return (
        <Background>
            <BackgroundBorder>
                <EdgePadding>
                    <H3 content="Something went wrong" className="text-center"/>
                    <Text className="mt-2" content={`Status: ${state?.status ?? 'Unknown'}`}/>
                    <Text content={`Message: ${state?.message ?? 'None'}`}/>
                    {state?.details && (
                        <pre className="mt-4 p-2 rounded text-sm overflow-auto">
                            {JSON.stringify(state.details, null, 2)}
                        </pre>
                    )}
                </EdgePadding>
            </BackgroundBorder>
        </Background>
    )
}

export function navToErrorPage(nav, err) {
    nav("/error", {
        state: {
            status: err.response?.status,
            message: err.message,
            details: err.response?.data
        }
   });
}
