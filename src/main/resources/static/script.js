const createApp = Vue.createApp;

const app = createApp({
	template: `
		<h1 v-if="loginUser">hello, {{loginUser}}!</h1>
		<h1 v-else>hello, world!</h1>

		<div v-if="message">{{message}}</div>

		<div v-if="!posts">
			fetching posts...
		</div>
		<div v-else-if="posts.length">
			<div class="post single" v-if="post">
				<div class="single__inner">
					<button class="single__close" @click="closePost">
						<span></span>
						<span></span>
					</button>
					<img class="post__image" :src="'/api/v1/post/' + post.id + '/image'">
					<div class="post__content">{{post.content}}</div>
					<ul class="comments post__comments" v-if="post.comments">
						<li class="comments__comment" v-for="comment in post.comments">
							{{comment.commenter.username}}: {{comment.content}}
						</li>
					</ul>
					<form class="form post__form" v-if="loginUser" @submit.prevent="addComment(post)">
						<div class="form__row">
							<label>
								comment
								<input v-model="comment"/>
							</label>
						</div>
						<div class="form__row">
							<button class="form__button">add</button>
						</div>
					</form>
				</div>
			</div>

			<div class="post" v-for="post in posts" @click.prevent="viewPost(post)">
				<img class="post__image" :src="'/api/v1/post/' + post.id + '/image'">
				<div class="post__content">{{post.content}}</div>
				<ul class="comments post__comments"v-if="post.comments">
					<li class="comments__comment" v-for="comment in post.comments.slice(0, 2)">
						{{comment.commenter.username}}: {{comment.content}}
					</li>
				</ul>
			</div>
		</div>
		<div v-else>
			no posts
		</div>

		<div v-if="loginCheck && !loginUser">
			<form @submit.prevent="login">
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
		</div>
		<div v-if="loginUser">
			<form @submit.prevent="postPost">
				<div>
					<label>
						image
						<input type="file" @change="setPostImage" ref="imageInput"/>
					</label>
				</div>
				<div>
					<label>
						content
						<input v-model="postContent"/>
					</label>
				</div>
				<div>
					<button>post</button>
				</div>
			</form>
			<form @submit.prevent="logout">
				<div>
					<button>logout</button>
				</div>
			</form>
		</div>
	`,
	data() {
		return {
			message: null,
			messageTimeout: null,

			post: null,

			posts: null,
			comment: null,

			postImage: null,
			postContent: null,

			loginCheck: false,
			loginUser: null,
			username: null,
			password: null,
		};
	},
	mounted() {
		this.fetchPosts();
		this.checkLogin();
	},
	methods: {
		showMessage(message) {
			this.message = message;
			clearTimeout(this.messageTimeout);
			this.messageTimeout = setTimeout(() => this.message = null, 2000);
		},
		fetchPosts() {
			fetch("/api/v1/post/list")
				.then(res => res.json())
				.then(json => {
					this.posts = json.posts;
				})
				.catch(console.error);
		},
		fetchPost(id) {
			fetch(`/api/v1/post/${id}`)
				.then(res => res.json())
				.then(json => {
					this.post = json;
				})
				.catch(console.error);
		},
		addComment(post) {
			fetch("/api/v1/comment/add", {
				method: "POST",
				headers: {
					"Content-Type": "application/json",
				},
				body: JSON.stringify({
					post: post.id,
					content: this.comment,
				}),
			})
				.then(res => res.text())
				.then(text => {
					this.comment = null;
					this.fetchPost(post.id);
				})
				.catch(console.error);
		},
		viewPost(post) {
			this.fetchPost(post.id);
		},
		closePost() {
			this.post = null;
		},
		setPostImage(event) {
			this.postImage = event.target.files[0];
		},
		postPost() {
			const formData = new FormData();
			formData.append("file", this.postImage);
			formData.append("request", new Blob(
				[JSON.stringify({content: this.postContent})],
				{type: "application/json"},
			));
			fetch("/api/v1/post/post", {
				method: "POST",
				body: formData,
			})
				.then(res => res.text())
				.then(text => {
					this.postImage = null;
					this.postContent = null;
					this.$refs.imageInput.value = null;
					this.fetchPosts();
				})
				.catch(console.error);
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
