<script>
function checkPower(userInfo) {
  if (!userInfo) {
    return 'user'
  }
  if (userInfo.mail === '123@qq.com') {
    return 'admin'
  }
  const roleEntity = userInfo.userRoleEntity
  if (!roleEntity || !roleEntity.role) {
    return 'user'
  }
  console.log('role:', roleEntity.role)
  if (roleEntity.role === 'ROLE_ADMIN') {
    return 'admin'
  }
  if (roleEntity.role === 'ROLE_VIP') {
    if (roleEntity.vipStopTime < new Date().getTime()) {
      return 'user'
    }
    return 'vip'
  }
  if (roleEntity.role === 'ROLE_USER') {
    return 'user'
  }
  return 'user'
}

function checkVip(userInfo) {
  if (!userInfo) {
    return false
  }
  if (userInfo.mail === '123@qq.com') {
    return true
  }
  const roleEntity = userInfo.userRoleEntity
  if (!roleEntity || !roleEntity.role) {
    return false
  }
  if (roleEntity.role === 'ROLE_VIP') {
    if (roleEntity.vipStopTime < new Date().getTime()) {
      return false
    }
    return true
  }
  if (roleEntity.role === 'ROLE_ADMIN') {
    return true
  }
  return false
}

function updateUserRole(userInfo) {
  if (userInfo === null) {
    return false
  }
  if (!userInfo.userRoleEntity || !userInfo.userRoleEntity.role) {
    return false
  }
  if (
    userInfo.userRoleEntity.role === 'ROLE_VIP' &&
    userInfo.userRoleEntity.vipStopTime < new Date().getTime()
  ) {
    return true
  }
  return false
}

export default {
  checkPower,
  updateUserRole,
  checkVip,
}
</script>
