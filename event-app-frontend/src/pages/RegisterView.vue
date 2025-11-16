<!--
  This file represents the *page* for the register route.

  It is very small on purpose:
  - All the real UI and logic live in the RegisterHero organism.
  - This page only imports that component and displays it.
-->

<script setup>
  // ---------------------------------------------------------
  // IMPORTS
  // ---------------------------------------------------------

  // Import the RegisterHero component.
  //
  // '@' is a path alias that usually points to 'src/'.
  // So '@/components/organisms/RegisterHero.vue'
  // really means 'src/components/organisms/RegisterHero.vue'.
  import RegisterHero from '@/components/organisms/RegisterHero.vue'
  // Because we are using <script setup>, we don't need to
  // manually register components in a "components: { ... }" block.
  // Simply importing it is enough to use <RegisterHero /> in the template.
</script>

<template>
  <!--
    The root element of this page.

    <main> is a semantic HTML tag used for the main content of a page.
    Here we also apply Tailwind / daisyUI utility classes:
      - min-h-screen : the element's minimum height = full viewport height
      - bg-base-200  : background color from daisyUI theme (light gray)
  -->
  <main class="min-h-screen bg-base-200">
    <!--
      Insert our RegisterHero organism.

      The organism already contains:
        - The hero layout
        - The registration card
        - The whole form + Yup validation

      This keeps the page file clean and lets us reuse the organism
      somewhere else if we ever want to.
    -->
    <RegisterHero />
  </main>
</template>

<style scoped>
  /*
    We don't define extra CSS here because almost all styling is
    handled with Tailwind / daisyUI classes directly in the template.

    "scoped" means: if we added any CSS rules here, they would apply
    only to this component (RegisterView) and not leak to other components.
  */
</style>
