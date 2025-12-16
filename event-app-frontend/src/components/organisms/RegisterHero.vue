<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import * as yup from 'yup'

defineOptions({
  name: 'RegisterHero',
})

const router = useRouter()

const form = reactive({
  salutation: '',
  salutationOther: '',
  email: '',
  username: '',
  password: '',
  repeatPassword: '',
  country: '',
})

const errors = reactive({})
const submitError = ref('')
const submitSuccess = ref(false)

const dachCountries = ['Germany', 'Austria', 'Switzerland']
const otherCountries = [
  'Afghanistan','Albania','Algeria','Andorra','Angola','Argentina','Armenia',
  'Australia','Azerbaijan','Bahamas','Bahrain','Bangladesh','Belarus','Belgium',
  'Brazil','Canada','China','Denmark','Egypt','Finland','France','Greece','India',
  'Indonesia','Iran','Iraq','Ireland','Israel','Italy','Japan','Kenya','Mexico',
  'Netherlands','Norway','Poland','Portugal','Russia','Saudi Arabia','Spain',
  'Sweden','Thailand','Turkey','Ukraine','United Kingdom','United States',
  'Vietnam','Zambia','Zimbabwe'
]

const schema = yup.object({
  salutation: yup.string().required(),
  salutationOther: yup.string().when('salutation', {
    is: (value) => value === 'other',
    then: (schema) => schema.required().max(30),
    otherwise: (schema) => schema.max(30),
  }),
  email: yup.string().required().email(),
  username: yup.string().required(),
  password: yup
    .string()
    .required()
    .min(12)
    .matches(/[a-z]/)
    .matches(/[A-Z]/)
    .matches(/\d/)
    .matches(/[^A-Za-z0-9]/),
  repeatPassword: yup
    .string()
    .required()
    .oneOf([yup.ref('password')]),
  country: yup.string().required(),
})

async function handleSubmit() {
  submitError.value = ''
  submitSuccess.value = false
  Object.keys(errors).forEach((key) => (errors[key] = ''))

  try {
    const validData = await schema.validate(form, { abortEarly: false })
    submitSuccess.value = true
    console.log('Valid registration data:', validData)

    // Redirect to login page after successful registration
    await router.push('/login')
  } catch (err) {
    if (Array.isArray(err.inner)) {
      err.inner.forEach((e) => {
        if (!errors[e.path]) errors[e.path] = e.message
      })
    } else if (err.message) {
      submitError.value = err.message
    } else {
      submitError.value = 'Unexpected validation error.'
    }
  }
}
</script>

<template>
  <section class="hero min-h-screen bg-base-200">
    <div class="hero-content flex-col lg:flex-row-reverse gap-12 w-full">
      <div class="card bg-base-100 w-full max-w-md shadow-2xl">
        <form class="card-body space-y-4" @submit.prevent="handleSubmit">
          <h2 class="card-title">Create your account</h2>

          <div class="form-control">
            <label class="label" for="salutation">
              <span class="label-text font-semibold">Salutation</span>
            </label>
            <select
              id="salutation"
              v-model="form.salutation"
              :class="['select select-bordered w-full', { 'select-error': errors.salutation }]"
            >
              <option disabled value="">Choose: male, female, other</option>
              <option value="male">male</option>
              <option value="female">female</option>
              <option value="other">other</option>
            </select>
            <p v-if="errors.salutation" class="text-error text-xs mt-1">{{ errors.salutation }}</p>
          </div>

          <div v-if="form.salutation === 'other'" class="form-control">
            <label class="label" for="salutationOther">
              <span class="label-text font-semibold">Please specify (max 30 chars)</span>
            </label>
            <input
              id="salutationOther"
              v-model="form.salutationOther"
              maxlength="30"
              type="text"
              :class="['input input-bordered w-full', { 'input-error': errors.salutationOther }]"
            />
            <div class="flex justify-end text-xs text-base-content/60">
              {{ form.salutationOther.length }}/30
            </div>
            <p v-if="errors.salutationOther" class="text-error text-xs mt-1">
              {{ errors.salutationOther }}
            </p>
          </div>

          <div class="form-control">
            <label class="label" for="email">
              <span class="label-text font-semibold">Email</span>
            </label>
            <input
              id="email"
              v-model="form.email"
              type="email"
              placeholder="your@email.com"
              :class="['input input-bordered w-full', { 'input-error': errors.email }]"
            />
            <p v-if="errors.email" class="text-error text-xs mt-1">{{ errors.email }}</p>
          </div>

          <div class="form-control">
            <label class="label" for="username">
              <span class="label-text font-semibold">Username</span>
            </label>
            <input
              id="username"
              v-model="form.username"
              type="text"
              placeholder="username"
              :class="['input input-bordered w-full', { 'input-error': errors.username }]"
            />
            <p v-if="errors.username" class="text-error text-xs mt-1">{{ errors.username }}</p>
          </div>

          <div class="form-control">
            <label class="label" for="password">
              <span class="label-text font-semibold">Password</span>
            </label>
            <input
              id="password"
              v-model="form.password"
              type="password"
              placeholder="********"
              :class="['input input-bordered w-full', { 'input-error': errors.password }]"
            />
            <p v-if="errors.password" class="text-error text-xs mt-1">{{ errors.password }}</p>
          </div>

          <div class="form-control">
            <label class="label" for="repeatPassword">
              <span class="label-text font-semibold">Repeat Password</span>
            </label>
            <input
              id="repeatPassword"
              v-model="form.repeatPassword"
              type="password"
              placeholder="********"
              :class="['input input-bordered w-full', { 'input-error': errors.repeatPassword }]"
            />
            <p v-if="errors.repeatPassword" class="text-error text-xs mt-1">
              {{ errors.repeatPassword }}
            </p>
          </div>

          <div class="form-control">
            <label class="label" for="country">
              <span class="label-text font-semibold">Country</span>
            </label>
            <select
              id="country"
              v-model="form.country"
              :class="['select select-bordered w-full', { 'select-error': errors.country }]"
            >
              <option disabled value="">Select a country</option>
              <optgroup label="DACH">
                <option v-for="c in dachCountries" :key="c" :value="c">{{ c }}</option>
              </optgroup>
              <optgroup label="Other countries">
                <option v-for="c in otherCountries" :key="c" :value="c">{{ c }}</option>
              </optgroup>
            </select>
            <p v-if="errors.country" class="text-error text-xs mt-1">{{ errors.country }}</p>
          </div>

          <p v-if="submitError" class="text-error text-sm">{{ submitError }}</p>

          <p v-if="submitSuccess" class="text-success text-sm">
            Form is valid.
          </p>

          <div class="card-actions justify-end mt-2">
            <button type="submit" class="btn btn-ghost normal-case rounded-full px-6">
              Register
            </button>
          </div>
        </form>
      </div>

      <div class="text-center lg:text-left max-w-md">
        <h1 class="text-5xl font-bold">Register</h1>
        <p class="py-6">
          Enter your data to create your account.
        </p>
      </div>
    </div>
  </section>
</template>

