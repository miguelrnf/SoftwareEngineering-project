<template>
  <v-card class="mx-auto mt-4 d-flex flex-wrap" width="95%">
    <v-navigation-drawer absolute permanent dense color="primary">
      <v-row class="white--text mt-5 ml-4 headline">
        <v-icon color="white" class="mr-12">fas fa-align-justify</v-icon>
        {{ 'Categories' }}
      </v-row>
      <v-divider class="mt-5 white"></v-divider>
      <v-list>
        <v-list-item v-for="item in categories" :key="item.title" link>
          <v-list-item-icon>
            <v-icon color="white">{{ item.icon }}</v-icon>
          </v-list-item-icon>

          <v-list-item-content class="white--text">
            <v-list-item-title>{{ item.title }}</v-list-item-title>
          </v-list-item-content>
        </v-list-item>
      </v-list>
      <template v-slot:append>
        <v-row class="white--text pa-5 ml-0">
          <v-icon color="white" class="mr-12">fas fa-coins</v-icon>
          {{ $store.getters.getUser.score + ' Achandos' }}
        </v-row>
      </template>
    </v-navigation-drawer>
    <v-row no-gutters>
      <v-col cols="12" md="4" v-for="n in 9" :key="n">
        <v-card outlined tile height="237px">
          {{ 'asdas' }}
        </v-card>
      </v-col>
    </v-row>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';

@Component
export default class ShopHomeView extends Vue {
  categories = [
    { title: 'Background Color', icon: 'fas fa-paint-roller' },
    { title: 'Power Ups', icon: 'fas fa-rocket' },
    { title: 'Post Awards', icon: 'fas fa-star' }
  ];
}
</script>
