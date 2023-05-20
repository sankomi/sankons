const createApp = Vue.createApp;

const app = createApp({
	template: `
		<h1 v-if="loginUser">hello, {{loginUser}}!</h1>
		<h1 v-else>hello, world!</h1>

		<div v-if="message">{{message}}</div>

		<form v-if="loginCheck && !loginUser" @submit.prevent="login">
			<div>
				<label>
					username
					<input v-model="username"/>
				</label>
			</div>
			<div>
				<label>
					password
					<input type="password" v-model="password"/>
				</label>
			</div>
			<div>
				<button>login</button>
			</div>
		</form>
		<form v-if="loginUser" @submit.prevent="logout">
			<div>
				<button>logout</button>
			</div>
		</form>
	`,
	data() {
		return {
			loginCheck: false,
			loginUser: null,
			username: null,
			password: null,
			message: null,
			messageTimeout: null,
		};
	},
	mounted() {
		this.checkLogin();
	},
	methods: {
		showMessage(message) {
			this.message = message;
			clearTimeout(this.messageTimeout);
			this.messageTimeout = setTimeout(() => this.message = null, 2000);
		},
		checkLogin() {
			fetch("/api/v1/user/login")
				.then(res => res.text())
				.then(text => {
					if (text) {
						this.loginUser = text;
					} else {
						this.loginUser = null;
					}
					this.loginCheck = true;
				})
				.catch(console.error);
		},
		login() {
			fetch("/api/v1/user/login", {
				method: "POST",
				headers: {
					"Content-Type": "application/json",
				},
				body: JSON.stringify({
					username: this.username,
					password: this.password,
				}),
			})
				.then(res => res.text())
				.then(text => {
					if (text === "true") {
						this.showMessage("login success");
					} else {
						this.showMessage("login fail");
					}
					this.checkLogin();
				})
				.catch(console.error);
		},
		logout() {
			fetch("/api/v1/user/login", {
				method: "DELETE",
			})
				.then(res => res.text())
				.then(text => {
					this.showMessage("logout success");
					this.checkLogin();
				})
				.catch(console.error);
		},
	},
}).mount("#app");
