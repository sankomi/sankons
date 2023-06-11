const createApp = Vue.createApp;

const app = createApp({
	template: `
		<h1 class="title" v-if="loginUser">hello, {{loginUser}}!</h1>
		<h1 class="title" v-else>hello, world!</h1>

		<div v-if="!posts">
			<p>fetching posts...</p>
		</div>
		<div v-else-if="posts.length">
			<Transition name="single">
				<div class="single" v-if="post">
					<div class="post single__inner">
						<div class="single__user post__user">
							{{post.poster.username}}
							<button class="single__close" @click="closePost">
								<span></span>
								<span></span>
							</button>
						</div>
						<figure class="single__image post__image">
							<img :src="'/api/v1/post/' + post.id + '/image'">
						</figure>
						<div class="post__stats">
							views {{post.views}}
							<button class="post__like" :class="{'post__like--liked': post.like}" @click="like(post)">
								<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 8 8"><path d="M4 7.33l2.952-2.954c.918-.918.9-2.256.066-3.087A2.134 2.134 0 0 0 4 1.29a2.132 2.132 0 0 0-3.015-.01C.15 2.12.13 3.46 1.05 4.372L4 7.33z"/></svg>
							</button>
							likes {{post.likes}}
						</div>
						<div class="post__content">
							<p>{{post.content}}</p>
						</div>
						<div v-if="post.comments.length" class="single__comment-outer post__comment-outer">
							<ul class="single__comments comments post__comments" v-if="post.comments">
								<li class="comments__comment" v-for="comment in post.comments">
									{{comment.commenter.username}}: {{comment.content}}
								</li>
							</ul>
							<form v-if="moreComments" class="single__comment-form form form--more" @click.prevent="fetchComments(post)">
								<div class="form__row">
									<button class="button form__button">more</button>
								</div>
							</form>
						</div>
						<div v-else class="post__comment-outer">
							no comments
						</div>
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
			</Transition>

			<div class="posts">
				<div class="post posts__post" v-for="post in posts" @click.prevent="viewPost(post)">
					<div class="post__user">{{post.poster.username}}</div>
					<figure class="post__image">
						<img :src="'/api/v1/post/' + post.id + '/image'">
					</figure>
					<div class="post__stats">
						views {{post.views}}
						<button class="post__like" :class="{'post__like--liked': post.like}" @click.stop="like(post)">
							<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 8 8"><path d="M4 7.33l2.952-2.954c.918-.918.9-2.256.066-3.087A2.134 2.134 0 0 0 4 1.29a2.132 2.132 0 0 0-3.015-.01C.15 2.12.13 3.46 1.05 4.372L4 7.33z"/></svg>
						</button>
						likes {{post.likes}}
					</div>
					<div class="post__content">
						<p>{{post.content}}</p>
					</div>
					<div v-if="post.comments.length" class="post__comment-outer">
						<ul class="comments post__comments" v-if="post.comments">
							<li class="comments__comment" v-for="comment in post.comments">
								{{comment.commenter.username}}: {{comment.content}}
							</li>
						</ul>
					</div>
					<div v-else class="post__comment-outer">
						no comments
					</div>
				</div>
			</div>

			<form v-if="morePosts" class="form form--more" @click.prevent="fetchPosts">
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
			moreComments: true,

			posts: null,
			currentPost: 0,
			morePosts: true,

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
				length: 5,
				commentLength: 5,
			}))
				.then(res => res.json())
				.then(json => {
					this.morePosts = this.currentPost + 5 <= json.end;
					this.currentPost = json.end;

					let from = 0;

					if (this.posts) {
						from = this.posts.length;
						this.posts.push(...json.posts);
					} else {
						this.posts = json.posts;
					}

					this.posts.filter((post, index) => {
						return index >= from;
					}).forEach(this.checkLike);
				})
				.catch(console.error);
		},
		fetchPost(post) {
			fetch(`/api/v1/post/${post.id}`)
				.then(res => res.json())
				.then(json => {
					this.post = json;
					this.currentComment = this.post.comments.length;

					this.moreComments = true;
					this.fetchComments(this.post);
					this.checkLike(this.post);
				})
				.catch(console.error);
		},
		checkLike(post) {
			fetch(`/api/v1/post/${post.id}/like`)
				.then(res => res.json())
				.then(json => {
					post.like = json.liked;
					post.likes = json.likes;
				})
				.catch(console.error);
		},
		fetchComments(post) {
			fetch("/api/v1/comment/list?" + new URLSearchParams({
				post: post.id,
				start: this.currentComment,
				length: 10,
			}))
				.then(res => res.json())
				.then(json => {
					this.moreComments = this.currentComment + 10 <= json.end;
					this.currentComment = json.end;

					this.post.comments.push(...json.comments);
				})
				.catch(console.error);
		},
		like(post) {
			fetch("/api/v1/post/like", {
				method: "PUT",
				headers: {
					"Content-Type": "application/json",
				},
				body: JSON.stringify({
					post: post.id,
				}),
			})
				.then(res => res.json())
				.then(json => {
					post.like = json.liked;
					post.likes = json.likes;
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
					this.post.comments.length = 0;
					this.currentComment = 0;
					this.moreComments = true;
					this.fetchComments(post);
				})
				.catch(console.error);
		},
		viewPost(post) {
			if (this.post) return;

			this.fetchPost(post);
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

					this.morePosts = true;
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
