import React, { useEffect, useState, useRef } from 'react';
import axiosInstance from "../axiosInstance";

function UserControl() {
    const [users, setUsers] = useState([]);
    const [nextCursor, setNextCursor] = useState(null); // 다음 페이지를 요청할 커서 정보
    const [isLoading, setIsLoading] = useState(false);
    const [hasMore, setHasMore] = useState(true); // 더 많은 데이터가 있는지 여부
    const hasFetched = useRef(false); // fetchUserControl이 한 번만 호출되었는지 추적

    // 회원 목록을 가져오는 함수
    const fetchUserControl = async (cursor = null) => {
        try {
            setIsLoading(true);
            const params = {
                limit: 10, // 요청할 데이터 수
            };

            if (cursor) {
                params.lastCreatedAt = cursor.lastCreatedAt;
                params.lastId = cursor.lastId;
            }

            const response = await axiosInstance.get(`/api/adm/members/all/cursor`, {
                params: params,
            });

            console.log('API Response:', response.data); // 응답 로그

            const newUsers = response.data.members;
            setUsers(prevUsers => [...prevUsers, ...newUsers]); // 기존 사용자 목록에 새 사용자 추가

            if (response.data.nextCursor) {
                setNextCursor(response.data.nextCursor); // 다음 페이지를 위한 커서 업데이트
            } else {
                setHasMore(false); // 더 이상 데이터가 없음을 표시
            }
        } catch (error) {
            alert('사용자 목록을 불러오는 데 실패했습니다: ' + (error.response?.data?.message || '알 수 없는 오류'));
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        if (!hasFetched.current) {
            console.log(`Fetching userControl with cursor: ${JSON.stringify(nextCursor)}`);
            fetchUserControl(); // 초기 데이터 로드 (커서 없음)
            hasFetched.current = true; // 한 번 호출되었음을 표시
        }
    }, []); // 컴포넌트 마운트 시 한 번 실행

    const handleNext = () => {
        if (hasMore && !isLoading) {
            fetchUserControl(nextCursor);
        }
    };

    return (
        <div>
            <h2>사용자 목록</h2>
            <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                <thead>
                <tr>
                    <th style={{ width: '25%', padding: '8px', textAlign: 'left' }}>회원명</th>
                    <th style={{ width: '25%', padding: '8px', textAlign: 'left' }}>가입 ID</th>
                </tr>
                </thead>
                <tbody>
                {users.length > 0 ? (
                    users.map((user, index) => (
                        <tr key={index}>
                            <td style={{ padding: '8px', border: '1px solid #ccc' }}>{user.name}</td>
                            <td style={{ padding: '8px', border: '1px solid #ccc' }}>{user.loginId}</td>
                        </tr>
                    ))
                ) : (
                    <tr>
                        <td colSpan="2" style={{ textAlign: 'center', padding: '8px' }}>사용자가 없습니다.</td>
                    </tr>
                )}
                </tbody>
            </table>
            <div style={{ marginTop: '16px' }}>

                <button
                    onClick={handleNext}
                    disabled={!hasMore || isLoading}
                >
                    {isLoading ? '로딩 중...' : '다음'}
                </button>

            </div>
        </div>
    );
}

export default UserControl;
