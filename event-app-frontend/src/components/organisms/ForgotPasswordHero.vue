<template>
  <section class="forgot-wrap">
    <div class="card">
      <h2 class="title">Forgot your password?</h2>
      <p class="subtitle">
        Enter your email address and we’ll send you a link to reset your password.
      </p>

      <form @submit.prevent="onSubmit" novalidate>
        <label class="label" for="email">Email</label>
        <input
          id="email"
          class="input"
          type="email"
          v-model.trim="form.email"
          autocomplete="email"
          placeholder="you@example.com"
        />
        <p v-if="errors.email" class="error">{{ errors.email }}</p>

        <p v-if="submitError" class="error">{{ submitError }}</p>
        <p v-if="submitSuccess" class="success">
          If an account exists for that email, a reset link has been sent.
        </p>

        <button class="btn" type="submit" :disabled="isSubmitting">
          {{ isSubmitting ? "Sending..." : "Send reset link" }}
        </button>

        <div class="links">
          <RouterLink to="/login">Back to login</RouterLink>
          <RouterLink to="/register">Create account</RouterLink>
        </div>
      </form>
    </div>
  </section>
</template>

<script setup>
import { reactive, ref } from "vue";
import * as yup from "yup";

const form = reactive({
  email: "",
});

const errors = reactive({
  email: "",
});

const isSubmitting = ref(false);
const submitError = ref("");
const submitSuccess = ref(false);

const schema = yup.object({
  email: yup
    .string()
    .email("Please enter a valid email.")
    .required("Email is required."),
});

function clearErrors() {
  errors.email = "";
  submitError.value = "";
}

async function onSubmit() {
  clearErrors();
  submitSuccess.value = false;

  
  try {
    await schema.validate(form, { abortEarly: false });
  } catch (validationErr) {
    if (validationErr?.inner?.length) {
      for (const e of validationErr.inner) {
        if (e.path && e.message) errors[e.path] = e.message;
      }
    } else if (validationErr?.message) {
      submitError.value = validationErr.message;
    }
    return;
  }

  
  isSubmitting.value = true;

  try {
    const res = await fetch("http://localhost:3000/auth/forgot-password", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email: form.email }),
    });


    if (!res.ok) {
      
      // return;
    }

    submitSuccess.value = true;
  } catch  {
    
    submitError.value = "Unable to send reset link right now. Please try again later.";
  } finally {
    isSubmitting.value = false;
  }
}
</script>

<style scoped>

.forgot-wrap {
  min-height: calc(100vh - 90px);
  display: grid;
  place-items: center;
  padding: 32px 16px;
}

.card {
  width: min(520px, 100%);
  background: #fff;
  border-radius: 14px;
  box-shadow: 0 18px 45px rgba(0, 0, 0, 0.12);
  padding: 24px;
}

.title {
  margin: 0 0 6px;
  font-size: 28px;
  font-weight: 800;
}

.subtitle {
  margin: 0 0 18px;
  opacity: 0.85;
}

.label {
  display: block;
  font-size: 13px;
  margin: 10px 0 6px;
  opacity: 0.8;
}

.input {
  width: 100%;
  height: 44px;
  border-radius: 999px;
  border: 1px solid rgba(0, 0, 0, 0.2);
  padding: 0 14px;
  outline: none;
}

.input:focus {
  border-color: rgba(0, 0, 0, 0.5);
}

.btn {
  width: 100%;
  height: 44px;
  border: none;
  border-radius: 999px;
  margin-top: 16px;
  cursor: pointer;
  background: #1f1f1f;
  color: #fff;
  font-weight: 700;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.error {
  color: #b00020;
  font-size: 13px;
  margin: 8px 0 0;
}

.success {
  color: #0b6b2f;
  font-size: 13px;
  margin: 8px 0 0;
}

.links {
  margin-top: 14px;
  display: flex;
  justify-content: space-between;
  font-size: 14px;
}
</style>

