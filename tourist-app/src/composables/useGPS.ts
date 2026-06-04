import { ref, onUnmounted } from 'vue'

/**
 * GPS定位 + 地理围栏上报
 */
export function useGPS() {
  let timer: any = null

  function startTracking() {
    timer = setInterval(async () => {
      try {
        const loc = await new Promise<any>((resolve, reject) => {
          uni.getLocation({
            type: 'gcj02',
            success: resolve,
            fail: reject
          })
        })

        // 上报位置，查询围栏
        const res = await uni.request({
          url: '/api/v1/tourist/position/report',
          method: 'POST',
          data: { latitude: loc.latitude, longitude: loc.longitude, scenicId: 1, userId: 1 }
        })

        const spot = (res.data as any)?.data
        if (spot) {
          console.log('进入景点围栏:', spot.spotName)
          // TODO: 自动触发AI讲解
        }
      } catch (e) {
        console.log('GPS定位失败', e)
      }
    }, 5000) // 每5秒上报一次
  }

  function stopTracking() {
    if (timer) clearInterval(timer)
  }

  return { startTracking, stopTracking }
}
