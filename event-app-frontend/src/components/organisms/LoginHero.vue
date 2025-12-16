<script setup>
import { ref } from "vue";
import { RouterLink } from "vue-router";

import AtomButton from "../atoms/AtomButton.vue";
import AtomLink from "../atoms/AtomLink.vue";
import LabeledInput from "../molecules/LabeledInput.vue";
import MoleculeFieldset from "../molecules/MoleculeFieldset.vue";
import ErrorModal from "./ErrorModal.vue";

const email = ref("");
const password = ref("");
const showError = ref(false);
const errorMessage = ref("");

const handleLogin = () => {
  if (email.value !== "admin@example.com" || password.value !== "password") {
    errorMessage.value = "Invalid email or password. Please try again.";
    showError.value = true;
    return;
  }

  showError.value = false;
};

const handleErrorClose = () => {
  showError.value = false;
};
</script>

<template>
  <div class="hero bg-base-200 min-h-screen">
    <div class="hero-content flex-col lg:flex-row-reverse">
      <div class="text-center lg:text-left">
        <h1 class="text-5xl font-bold">Login now!</h1>
        <p class="py-6">
          If you are not registered yet, please check out the
          <AtomLink href="http://localhost:5173/register"> register page</AtomLink> !
        </p>
      </div>

      <div class="card bg-base-100 w-full max-w-sm shrink-0 shadow-2xl">
        <div class="card-body">
          <form @submit.prevent="handleLogin">
            <MoleculeFieldset>
              <LabeledInput
                v-model="email"
                type="email"
                InputClass="w-full"
                placeholder="Email"
                label="Email"
                id="email-input"
                name="email"
              />

              <LabeledInput
                v-model="password"
                type="password"
                InputClass="w-full"
                placeholder="password"
                label="Password"
                id="password-input"
                name="password"
              />


              <div class="mt-2">
                <RouterLink to="/forgot-password" class="link link-hover">
                  Forgot password?
                </RouterLink>
              </div>

              <AtomButton
                class="btn-neutral mt-4"
                label="Login"
                type="submit"
              />
            </MoleculeFieldset>
          </form>
        </div>
      </div>
    </div>

    <ErrorModal :show="showError" @close="handleErrorClose">
      <h3 class="font-bold text-lg">Login Error</h3>
      <p class="py-4">{{ errorMessage }}</p>
    </ErrorModal>
  </div>
</template>

<style scoped></style>

