<script setup>
import AtomButton from '../atoms/AtomButton.vue'
import AtomLink from '../atoms/AtomLink.vue'
import LabeledInput from '../molecules/LabeledInput.vue'
import MoleculeFieldset from '../molecules/MoleculeFieldset.vue'
</script>
<template>
  <div class="hero bg-base-200 min-h-screen">
    <div class="hero-content flex-col lg:flex-row-reverse">
      <div class="text-center lg:text-left">
        <h1 class="text-5xl font-bold">Login now!</h1>
        <p class="py-6">
          If you are not registered yet, please check out the <AtomLink href="http://localhost:5173/register"> register page</AtomLink> !
        </p>
      </div>

      <div class="card bg-base-100 w-full max-w-sm shrink-0 shadow-2xl">
        <div class="card-body">
          <MoleculeFieldset>
            <LabeledInput
              type="email"
              InputClass="w-full"
              placeholder="Email"
              label="Email"
              id="email-input"
              name="email"
            />

            <LabeledInput
              type="password"
              InputClass="w-full"
              placeholder="password"
              label="Password"
              id="password-input"
              name="password"
            />

            <div>
              <AtomLink class="link-hover">Forgot password?</AtomLink>
            </div>
            <AtomButton class="btn-neutral mt-4" label="Login" />
          </MoleculeFieldset>
        </div>
      </div>
    </div>
  </div>
</template>
<style scoped></style>
