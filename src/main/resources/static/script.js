const createApp = Vue.createApp;

const app = createApp({
	template: `
		<h1 class="title" v-if="loginUser">hello, {{loginUser}}!</h1>
		<h1 class="title" v-else>hello, world!</h1>

		<div v-if="!posts">
			<p>fetching posts...</p>
		</div>
		<div v-else-if="posts.length">
			<div class="post single" v-if="post">
				<div class="single__inner">
					<button class="single__close" @click="closePost">
						<span></span>
						<span></span>
					</button>
					<figure class="single__image post__image">
						<img :src="'/api/v1/post/' + post.id + '/image'">
					</figure>
					<div class="post__content">
						<p>{{post.content}}</p>
					</div>
					<ul class="comments single__comments post__comments" v-if="post.comments">
						<li class="comments__comment" v-for="comment in post.comments">
							{{comment.commenter.username}}: {{comment.content}}
						</li>
					</ul>
					<form class="form form--more"@click.prevent="fetchComments(post.id)">
						<div class="form__row">
							<button class="button form__button">more</button>
						</div>
					</form>
					<form class="form single__form" v-if="loginUser" @submit.prevent="addComment(post)">
						<div class="form__row">
							<label for="comment">comment</label>
							<input id="comment" v-model="comment"/>
						</div>
						<div class="form__row">
							<button class="button form__button">add</button>
						</div>
					</form>
				</div>
			</div>

			<div class="posts">
				<div class="post posts__post" v-for="post in posts" @click.prevent="viewPost(post)">
					<figure class="post__image">
						<img :src="'/api/v1/post/' + post.id + '/image'">
					</figure>
					<div class="post__content">
						<p>{{post.content}}</p>
					</div>
					<ul class="comments post__comments" v-if="post.comments">
						<li class="comments__comment" v-for="comment in post.comments">
							{{comment.commenter.username}}: {{comment.content}}
						</li>
					</ul>
				</div>
			</div>

			<form class="form form--more" @click.prevent="fetchPosts">
				<div class="form__row">
					<button class="button form__button">more</button>
				</div>
			</form>
		</div>
		<div v-else>
			<p>no posts</p>
		</div>

		<div class="menu" :class="{'menu--show': showMenu}">
			<button class="button menu__toggle" @click="toggleMenu">{{showMenu? "v close v": "^ menu ^"}}</button>
			<div class="menu__inner" v-if="loginCheck && !loginUser">
				<form class="form menu__form" @submit.prevent="login">
					<div class="form__row">
						<label for="username">username</label>
						<input id="username" v-model="username"/>
					</div>
					<div class="form__row">
						<label for="password">password</label>
						<input id="password" type="password" v-model="password"/>
					</div>
					<div class="form__row">
						<button class="button form__button">login</button>
					</div>
				</form>
			</div>
			<div class="menu__inner" v-if="loginUser">
				<form class="form menu__form" @submit.prevent="postPost">
					<div class="form__row">
						<label for="image">image</label>
						<input id="image" type="file" @change="setPostImage" ref="imageInput"/>
					</div>
					<div class="form__row">
						<label for="content">content</label>
						<input id="content" v-model="postContent"/>
					</div>
					<div class="form__row">
						<button class="button form__button">post</button>
					</div>
				</form>
				<form class="form menu__form" @submit.prevent="logout">
					<div class="form__row">
						<button class="button form__button">logout</button>
					</div>
				</form>
			</div>
		</div>
	`,
	data() {
		return {
			message: null,
			messageTimeout: null,

			post: null,
			comment: null,
			currentComment: 0,

			posts: null,
			currentPost: 0,

			postImage: null,
			postContent: null,

			showMenu: false,
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
			fetch("/api/v1/post/list?" + new URLSearchParams({
				start: this.currentPost,
			}))
				.then(res => res.json())
				.then(json => {
					this.currentPost = json.end;

					if (this.posts) {
						this.posts.push(...json.posts);
					} else {
						this.posts = json.posts;
					}
				})
				.catch(console.error);
		},
		fetchPost(id) {
			fetch(`/api/v1/post/${id}`)
				.then(res => res.json())
				.then(json => {
					this.post = json;
					this.currentComment = this.post.comments.length;
				})
				.catch(console.error);
		},
		fetchComments(postId) {
			fetch("api/v1/comment/list?" + new URLSearchParams({
				post: postId,
				start: this.currentComment,
			}))
				.then(res => res.json())
				.then(json => {
					this.currentComment = json.end;

					this.post.comments.push(...json.comments);
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
		toggleMenu() {
			this.showMenu = !this.showMenu;
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
