import { createPinia } from 'pinia';
import { createApp } from 'vue';
import App from './App.vue';
import { initializeApp } from './app/init';
import router from './app/router';
import './style.css';

async function bootstrap() {
    const app = createApp(App);

    app.use(createPinia());

    await initializeApp();

    app.use(router);

    app.mount('#app');
}

bootstrap();
