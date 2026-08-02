import http from 'k6/http';
import { check } from 'k6';

export const options = {
    vus: 100,
    duration: '30s',
};

const BASE_URL = 'http://localhost:8080';

const headers = {
    Authorization:
        'Bearer ACCESS_TOKEN',
};

export default function () {
    const res = http.get(
        `${BASE_URL}/v1/album/ASDF123/photos?page=0&size=20&sorting=POPULAR`,
        { headers }
    );

    check(res, {
        'status is 200': (r) => r.status === 200,
    });
}