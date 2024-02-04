- redis 실행

  docker run -it redis:6.2 -p 6379:6379

- 레디스 내부 진입

  docker exec -it a4da5 redis-cli

- 큐 리스트 확인

  scan 0

- 큐 내부 확인

  zscan users:queue:default:wait 0

- 큐 등록 API

  curl -X POST localhost:9010/api/v1/queue?user_id=105

- 허용 API

  curl -X POST localhost:9010/api/v1/queue/allow?count=1

- 대기열에 있는지 확인 API

  curl -X GET localhost:9010/api/v1/queue/allowed?user_id=101

- 랭크확인 API

  curl -X GET localhost:9010/api/v1/queue/rank?user_id=105

- 대기열 페이지 API

  localhost:9010/waiting-room?user_id=100&redirect_url=https://www.naver.com

- 윈도우 파워쉘 대기큐, 진입큐 체크 명령어

    while ($true) {
      Get-Date
      docker exec a4da redis-cli zcard user:queue:default:wait
      docker exec a4da redis-cli zcard users:queue:default:proceed
      Start-Sleep -Seconds 1
    }

- JEMETER 테스트

    Number of Threads : 30

    Ramp-up period : 10

    user_id : ${__Random(1,999999)}

    redirect_url : https://127.0.0.1:9000
    
