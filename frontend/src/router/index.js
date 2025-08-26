import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

// 路由组件
const Login = () => import('@/views/Login.vue')
const Layout = () => import('@/views/Layout.vue')
const Dashboard = () => import('@/views/Dashboard.vue')

// 基础信息管理
const GradeManage = () => import('@/views/basic/GradeManage.vue')
const ClassManage = () => import('@/views/basic/ClassManage.vue')
const TeacherManage = () => import('@/views/basic/TeacherManage.vue')
const StudentManage = () => import('@/views/basic/StudentManage.vue')
const CourseManage = () => import('@/views/basic/CourseManage.vue')

// 成绩管理
const ExamManage = () => import('@/views/grade/ExamManage.vue')
const ScoreEntry = () => import('@/views/grade/ScoreEntry.vue')
const ScoreStatistics = () => import('@/views/grade/ScoreStatistics.vue')

// 系统管理
const UserManage = () => import('@/views/system/UserManage.vue')
const RoleManage = () => import('@/views/system/RoleManage.vue')

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: Dashboard,
        meta: { title: '首页', icon: 'House' }
      },
      {
        path: 'basic/grade',
        name: 'GradeManage',
        component: GradeManage,
        meta: { title: '年级管理', icon: 'School', parent: '基础信息管理' }
      },
      {
        path: 'basic/class',
        name: 'ClassManage',
        component: ClassManage,
        meta: { title: '班级管理', icon: 'House', parent: '基础信息管理' }
      },
      {
        path: 'basic/teacher',
        name: 'TeacherManage',
        component: TeacherManage,
        meta: { title: '教师管理', icon: 'Avatar', parent: '基础信息管理' }
      },
      {
        path: 'basic/student',
        name: 'StudentManage',
        component: StudentManage,
        meta: { title: '学生管理', icon: 'UserFilled', parent: '基础信息管理' }
      },
      {
        path: 'basic/course',
        name: 'CourseManage',
        component: CourseManage,
        meta: { title: '课程管理', icon: 'Reading', parent: '基础信息管理' }
      },
      {
        path: 'grade/exam',
        name: 'ExamManage',
        component: ExamManage,
        meta: { title: '考试管理', icon: 'Document', parent: '成绩管理' }
      },
      {
        path: 'grade/score',
        name: 'ScoreEntry',
        component: ScoreEntry,
        meta: { title: '成绩录入', icon: 'Edit', parent: '成绩管理' }
      },
      {
        path: 'grade/statistics',
        name: 'ScoreStatistics',
        component: ScoreStatistics,
        meta: { title: '成绩统计', icon: 'PieChart', parent: '成绩管理' }
      },
      {
        path: 'system/user',
        name: 'UserManage',
        component: UserManage,
        meta: { title: '用户管理', icon: 'User', parent: '系统管理' }
      },
      {
        path: 'system/role',
        name: 'RoleManage',
        component: RoleManage,
        meta: { title: '角色管理', icon: 'UserFilled', parent: '系统管理' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 学生成绩管理系统` : '学生成绩管理系统'
  
  // 检查是否需要登录
  if (to.meta.requiresAuth === false) {
    next()
    return
  }
  
  // 检查登录状态
  if (!userStore.isLoggedIn) {
    if (to.path !== '/login') {
      ElMessage.warning('请先登录')
      next('/login')
    } else {
      next()
    }
    return
  }
  
  // 已登录用户访问登录页，重定向到首页
  if (to.path === '/login') {
    next('/')
    return
  }
  
  next()
})

export default router