<script setup>
// -----------------------------------------------------------
// IMPORTS
// -----------------------------------------------------------

// Import Vue's reactive() and ref() functions.
// reactive() is used for reactive objects (like form data).
// ref() is used for single reactive values (like booleans or strings).
import { reactive, ref } from 'vue'

// Import Yup — a validation library used to build schemas
// that describe rules for each field (required, min length, etc.)
import * as yup from 'yup'


// -----------------------------------------------------------
// COMPONENT NAME (OPTIONAL BUT GOOD PRACTICE)
// -----------------------------------------------------------

// defineOptions lets us set configuration such as name.
// This name appears inside Vue DevTools, making debugging easier.
defineOptions({
  name: 'RegisterHero',
})


// -----------------------------------------------------------
// FORM DATA (ALL USER INPUT FIELDS)
// -----------------------------------------------------------

// reactive() wraps an object so Vue tracks each key for changes.
// This form object stores ALL input values from the form.
const form = reactive({
  salutation: '',         // dropdown: "male", "female", "other"
  salutationOther: '',    // text field only visible when salutation = "other"
  email: '',              // user's email address
  username: '',           // username input
  password: '',           // main password field
  repeatPassword: '',     // must match password
  country: '',            // country select input
})


// -----------------------------------------------------------
// ERROR HANDLING STATE
// -----------------------------------------------------------

// errors object holds error messages for individual inputs.
// example: errors.email = "Email is required"
const errors = reactive({})

// submitError = error not tied to a specific field (rare)
const submitError = ref('')

// submitSuccess = becomes TRUE if the entire form passes validation
const submitSuccess = ref(false)


// -----------------------------------------------------------
// COUNTRY LISTS
// -----------------------------------------------------------

// These must appear FIRST in the country dropdown
const dachCountries = ['Germany', 'Austria', 'Switzerland']

// Very long list of countries for the "Other" group.
// These populate the <select> using a v-for loop.
const otherCountries = [
  // (list cut for brevity, but kept from previous version)
  'Afghanistan', 'Albania', 'Algeria', 'Andorra', 'Angola', 'Argentina',
  'Armenia', 'Australia', 'Azerbaijan', 'Bahamas', 'Bahrain', 'Bangladesh',
  'Belarus', 'Belgium', 'Brazil', 'Canada', 'China', 'Denmark', 'Egypt',
  'Finland', 'France', 'Greece', 'India', 'Indonesia', 'Iran', 'Iraq',
  'Ireland', 'Israel', 'Italy', 'Japan', 'Kenya', 'Mexico', 'Netherlands',
  'Norway', 'Poland', 'Portugal', 'Russia', 'Saudi Arabia', 'Spain',
  'Sweden', 'Thailand', 'Turkey', 'Ukraine', 'United Kingdom',
  'United States', 'Vietnam', 'Zambia', 'Zimbabwe'
]


// -----------------------------------------------------------
// VALIDATION SCHEMA (THE MOST IMPORTANT PART)
// -----------------------------------------------------------

// Yup's object() function creates a schema describing validation rules
const schema = yup.object({
  // ---------------------------------------------------------
  // SALUTATION FIELD VALIDATION
  // ---------------------------------------------------------
  salutation: yup
    .string()                 // must be a string
    .required('Please choose a salutation.'), // cannot be empty

  // ---------------------------------------------------------
  // EXTRA TEXT FIELD (only when "other" is selected)
  // ---------------------------------------------------------
  salutationOther: yup.string().when('salutation', {
    // "is" defines when this rule applies
    is: (value) => value === 'other',

    // If salutation === "other", this field becomes REQUIRED
    then: (schema) =>
      schema
        .required('Please provide additional information (max 30 characters).')
        .max(30, 'Maximum 30 characters allowed.'),

    // If salutation is NOT "other", the field stays optional but still max 30
    otherwise: (schema) => schema.max(30),
  }),

  // ---------------------------------------------------------
  // EMAIL VALIDATION
  // ---------------------------------------------------------
  email: yup
    .string()
    .required('Email is required.')           // cannot be empty
    .email('Please enter a valid email address.'), // must be valid format

  // ---------------------------------------------------------
  // USERNAME VALIDATION
  // ---------------------------------------------------------
  username: yup
    .string()
    .required('Username cannot be empty.'),

  // ---------------------------------------------------------
  // PASSWORD VALIDATION
  // ---------------------------------------------------------
  password: yup
    .string()
    .required('Password is required.')
    .min(12, 'Password must be at least 12 characters long.')        // length rule
    .matches(/[a-z]/, 'Password must include a lowercase letter.')   // lowercase
    .matches(/[A-Z]/, 'Password must include an uppercase letter.')  // uppercase
    .matches(/\d/, 'Password must include a number.')                // digit
    .matches(
      /[^A-Za-z0-9]/,
      'Password must include a symbol (e.g. !, ?, #).'
    ), // special character

  // ---------------------------------------------------------
  // REPEAT PASSWORD MUST MATCH PASSWORD
  // ---------------------------------------------------------
  repeatPassword: yup
    .string()
    .required('Please repeat your password.')
    .oneOf([yup.ref('password')], 'Passwords must match.'),

  // ---------------------------------------------------------
  // COUNTRY
  // ---------------------------------------------------------
  country: yup.string().required('Please select a country.'),
})


// -----------------------------------------------------------
// FORM SUBMISSION FUNCTION
// -----------------------------------------------------------

// Called when the user clicks "Create Account"
async function handleSubmit() {
  // Clear previous submission state
  submitError.value = ''
  submitSuccess.value = false

  // Clear individual input errors from previous attempts
  Object.keys(errors).forEach((key) => (errors[key] = ''))

  try {
    // Validate form against Yup schema
    // abortEarly: false → collect ALL errors instead of stopping at first
    const validData = await schema.validate(form, { abortEarly: false })

    // If no errors: show success message
    submitSuccess.value = true

    // Log result (this is where you'd call your API)
    console.log('Valid registration data:', validData)
  } catch (err) {
    // Yup validation returns "err.inner" → an array of all field errors
    if (Array.isArray(err.inner)) {
      err.inner.forEach((e) => {
        // Only assign the first error found per field
        if (!errors[e.path]) errors[e.path] = e.message
      })
    } else if (err.message) {
      // fallback message
      submitError.value = err.message
    } else {
      submitError.value = 'Unexpected validation error.'
    }
  }
}
</script>



<!-- ---------------------------------------------------------
     TEMPLATE SECTION — EVERYTHING THE USER SEES
---------------------------------------------------------- -->

<template>
  <!-- Large hero banner taking full height of screen -->
  <section class="hero min-h-screen bg-base-200">

    <!-- Content container inside the hero -->
    <div class="hero-content flex-col lg:flex-row-reverse gap-12 w-full">

      <!-- ---------------------- RIGHT SIDE (FORM) ---------------------- -->
      <div class="card bg-base-100 w-full max-w-md shadow-2xl">

        <!-- Form wrapper; @submit.prevent stops page reload -->
        <form class="card-body space-y-4" @submit.prevent="handleSubmit">

          <!-- Form title -->
          <h2 class="card-title">Create your account</h2>

          <!-- Short explanation -->
          <p class="text-sm text-base-content/70">
            Fill all fields.
          </p>


          <!-- -------------------------------------------------------
               SALUTATION SELECT
          ------------------------------------------------------- -->
          <div class="form-control">
            <label class="label" for="salutation">
              <span class="label-text font-semibold">Salutation</span>
            </label>

            <!-- v-model binds dropdown value to form.salutation -->
            <select
              id="salutation"
              v-model="form.salutation"
              :class="[
                'select select-bordered w-full',
                { 'select-error': errors.salutation }
              ]"
            >
              <option disabled value="">Choose: male, female, other</option>
              <option value="male">male</option>
              <option value="female">female</option>
              <option value="other">other</option>
            </select>

            <!-- Error message below input -->
            <p v-if="errors.salutation" class="text-error text-xs mt-1">
              {{ errors.salutation }}
            </p>
          </div>


          <!-- -------------------------------------------------------
               SALUTATION OTHER TEXT FIELD (CONDITIONAL)
               Appears only if salutation === "other"
          ------------------------------------------------------- -->
          <div v-if="form.salutation === 'other'" class="form-control">
            <label class="label" for="salutationOther">
              <span class="label-text font-semibold">
                Please specify (max 30 chars)
              </span>
            </label>

            <input
              id="salutationOther"
              v-model="form.salutationOther"
              maxlength="30"
              type="text"
              :class="[
                'input input-bordered w-full',
                { 'input-error': errors.salutationOther }
              ]"
            />

            <!-- Character counter -->
            <div class="flex justify-end text-xs text-base-content/60">
              {{ form.salutationOther.length }}/30
            </div>

            <!-- Error message -->
            <p v-if="errors.salutationOther" class="text-error text-xs mt-1">
              {{ errors.salutationOther }}
            </p>
          </div>


          <!-- -------------------------------------------------------
               EMAIL FIELD
          ------------------------------------------------------- -->
          <div class="form-control">
            <label class="label" for="email">
              <span class="label-text font-semibold">Email</span>
            </label>

            <input
              id="email"
              v-model="form.email"
              type="email"
              placeholder="your@email.com"
              :class="[
                'input input-bordered w-full',
                { 'input-error': errors.email }
              ]"
            />

            <p v-if="errors.email" class="text-error text-xs mt-1">
              {{ errors.email }}
            </p>
          </div>


          <!-- -------------------------------------------------------
               USERNAME
          ------------------------------------------------------- -->
          <div class="form-control">
            <label class="label" for="username">
              <span class="label-text font-semibold">Username</span>
            </label>

            <input
              id="username"
              v-model="form.username"
              type="text"
              placeholder="username"
              :class="[
                'input input-bordered w-full',
                { 'input-error': errors.username }
              ]"
            />

            <p v-if="errors.username" class="text-error text-xs mt-1">
              {{ errors.username }}
            </p>
          </div>


          <!-- -------------------------------------------------------
               PASSWORD
          ------------------------------------------------------- -->
          <div class="form-control">
            <label class="label" for="password">
              <span class="label-text font-semibold">Password</span>
            </label>

            <input
              id="password"
              v-model="form.password"
              type="password"
              placeholder="********"
              :class="[
                'input input-bordered w-full',
                { 'input-error': errors.password }
              ]"
            />

            <p v-if="errors.password" class="text-error text-xs mt-1">
              {{ errors.password }}
            </p>
          </div>


          <!-- -------------------------------------------------------
               REPEAT PASSWORD
          ------------------------------------------------------- -->
          <div class="form-control">
            <label class="label" for="repeatPassword">
              <span class="label-text font-semibold">Repeat Password</span>
            </label>

            <input
              id="repeatPassword"
              v-model="form.repeatPassword"
              type="password"
              placeholder="********"
              :class="[
                'input input-bordered w-full',
                { 'input-error': errors.repeatPassword }
              ]"
            />

            <p v-if="errors.repeatPassword" class="text-error text-xs mt-1">
              {{ errors.repeatPassword }}
            </p>
          </div>


          <!-- -------------------------------------------------------
               COUNTRY SELECT
          ------------------------------------------------------- -->
          <div class="form-control">
            <label class="label" for="country">
              <span class="label-text font-semibold">Country</span>
            </label>

            <select
              id="country"
              v-model="form.country"
              :class="[
                'select select-bordered w-full',
                { 'select-error': errors.country }
              ]"
            >
              <option disabled value="">Select a country</option>

              <!-- Group 1: DACH -->
              <optgroup label="DACH">
                <option v-for="c in dachCountries" :key="c" :value="c">{{ c }}</option>
              </optgroup>

              <!-- Group 2: All other countries -->
              <optgroup label="Other countries">
                <option v-for="c in otherCountries" :key="c" :value="c">{{ c }}</option>
              </optgroup>
            </select>

            <p v-if="errors.country" class="text-error text-xs mt-1">
              {{ errors.country }}
            </p>
          </div>


          <!-- GENERAL ERROR MESSAGE -->
          <p v-if="submitError" class="text-error text-sm">
            {{ submitError }}
          </p>

          <!-- SUCCESS MESSAGE -->
          <p v-if="submitSuccess" class="text-success text-sm">
            Form is valid.
          </p>


          <!-- SUBMIT BUTTON -->
          <div class="card-actions justify-end mt-2">
            <button type="submit" class="btn btn-primary">Create Account</button>
          </div>

        </form>
      </div>


      <!-- ---------------------- LEFT SIDE (HERO TEXT) ---------------------- -->
      <div class="text-center lg:text-left max-w-md">
        <h1 class="text-5xl font-bold">Register</h1>
        <p class="py-6">
          Enter your data to create your account.
        </p>
      </div>

    </div>
  </section>
</template>


<style scoped>
/* No additional CSS needed because Tailwind/daisyUI covers styling */
/* scoped ensures these styles apply only to this component */
</style>

