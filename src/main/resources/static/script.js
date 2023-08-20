const createApp = Vue.createApp;

const app = createApp({
	template: `
		<Transition name="opacity" mode="out-in">
			<h1 class="title" v-if="tag">
				<button class="button title__cancel" @click="changeTag(null)">&lt;</button>
				#{{tag}}
			</h1>
			<h1 class="title" v-else-if="loginUser">hello, {{loginUser}}!</h1>
			<h1 class="title" v-else>hello, world!</h1>
		</Transition>

		<Transition name="popup">
			<div v-if="messageTimeout" class="popup popup--message">
				<div class="popup__box" @click="hideMessage">
					<p>{{message}}</p>
				</div>
			</div>
		</Transition>

		<Transition name="single">
			<div class="single" v-if="post">
				<div class="post single__inner">
					<div class="single__user post__user">
						<svg class="post__visibility" v-if="post.visibility === 'SELF'" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 120 120"><path d="M103.88 45.208c0-24.193-19.683-43.875-43.875-43.875-24.193 0-43.875 19.682-43.875 43.875H5v69.75h110v-69.75h-11.12zm-69.754 0c0-14.268 11.607-25.875 25.875-25.875S85.88 30.94 85.88 45.208H34.13zm31.75 32.918v20.06h-11.89V78.12c-2.485-1.828-4.105-4.766-4.105-8.09 0-5.55 4.5-10.05 10.06-10.05s10.05 4.5 10.05 10.05c0 3.324-1.62 6.264-4.105 8.094z"/></svg>
						{{post.poster.username}}
						<button v-if="!post.login" class="post__follow" :class="{'post__follow--following': post.following}" @click="follow(post)">
							<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 120 120"><path d="M41.504 39.537L60.062 0l18.556 39.538 41.502 6.34-30.032 30.775 7.088 43.457L60.06 99.593 22.947 120.11l7.09-43.457L.01 45.878z"/></svg>
						</button>
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
						<button class="post__like" :class="{'post__like--liked': post.like}" @click="likePost(post)">
							<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 8 8"><path d="M4 7.33l2.952-2.954c.918-.918.9-2.256.066-3.087A2.134 2.134 0 0 0 4 1.29a2.132 2.132 0 0 0-3.015-.01C.15 2.12.13 3.46 1.05 4.372L4 7.33z"/></svg>
						</button>
						likes {{post.likes}}
						<button v-if="post.login" class="post__delete" @click="deletePost(post)">
							<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 8 8"><g fill-rule="evenodd"><path d="M2.048.77l5.18 5.182L5.953 7.23.77 2.048 2.048.77z"/><path d="M5.952.77L7.23 2.05 2.048 7.23.77 5.952 5.953.772z"/></g></svg>
						</button>
					</div>
					<div class="post__content">
						<p>
							<span v-for="hashtag in post.hashtags">
								{{hashtag.text}}
								<a href="" @click.prevent.stop="changeTag(hashtag.tag.slice(1))">{{hashtag.tag}}</a>
							</span>
						</p>
					</div>
					<div v-if="post.comments.length" class="single__comment-outer post__comment-outer">
						<ul class="single__comments comments post__comments" v-if="post.comments">
							<li class="comments__comment" v-for="comment in post.comments">
								{{comment.commenter.username}}: {{comment.content}}
								<button v-if="comment.login" class="comments__delete" @click.stop="deleteComment(post, comment)">
									<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 8 8"><g fill-rule="evenodd"><path d="M2.048.77l5.18 5.182L5.953 7.23.77 2.048 2.048.77z"/><path d="M5.952.77L7.23 2.05 2.048 7.23.77 5.952 5.953.772z"/></g></svg>
								</button>
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

		<Transition name="opacity" mode="out-in">
			<div v-if="!posts">
				<p>fetching posts...</p>
			</div>
			<div v-else-if="posts.length">
				<div class="posts">
					<div class="post posts__post" v-for="post in posts" @click.prevent="viewPost(post)">
						<div class="post__user">
							<svg class="post__visibility" v-if="post.visibility === 'SELF'" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 120 120"><path d="M103.88 45.208c0-24.193-19.683-43.875-43.875-43.875-24.193 0-43.875 19.682-43.875 43.875H5v69.75h110v-69.75h-11.12zm-69.754 0c0-14.268 11.607-25.875 25.875-25.875S85.88 30.94 85.88 45.208H34.13zm31.75 32.918v20.06h-11.89V78.12c-2.485-1.828-4.105-4.766-4.105-8.09 0-5.55 4.5-10.05 10.06-10.05s10.05 4.5 10.05 10.05c0 3.324-1.62 6.264-4.105 8.094z"/></svg>
							{{post.poster.username}}
						</div>
						<figure class="post__image">
							<img :src="'/api/v1/post/' + post.id + '/image'">
						</figure>
						<div class="post__stats">
							views {{post.views}}
							<button class="post__like" :class="{'post__like--liked': post.like}" @click.stop="likePost(post)">
								<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 8 8"><path d="M4 7.33l2.952-2.954c.918-.918.9-2.256.066-3.087A2.134 2.134 0 0 0 4 1.29a2.132 2.132 0 0 0-3.015-.01C.15 2.12.13 3.46 1.05 4.372L4 7.33z"/></svg>
							</button>
							likes {{post.likes}}
							<button v-if="post.login" class="post__delete" @click.stop="deletePost(post)">
								<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 8 8"><g fill-rule="evenodd"><path d="M2.048.77l5.18 5.182L5.953 7.23.77 2.048 2.048.77z"/><path d="M5.952.77L7.23 2.05 2.048 7.23.77 5.952 5.953.772z"/></g></svg>
							</button>
						</div>
						<div class="post__content">
							<p>
								<span v-for="hashtag in post.hashtags">
									{{hashtag.text}}
									<a href="" @click.prevent.stop="changeTag(hashtag.tag.slice(1))">{{hashtag.tag}}</a>
								</span>
							</p>
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
		</Transition>

		<div class="menu" :class="{'menu--show': showMenu}">
			<button class="button menu__toggle" @click="toggleMenu">{{showMenu? "v close v": "^ menu ^"}}</button>
			<div class="menu__inner" v-if="loginCheck && !loginUser">
				<form class="form menu__form">
					<div class="form__row">
						<button class="button" @click.prevent="toggleLogin(true)">login</button>
					</div>
					<div class="form__row">
						<button class="button" @click.prevent="toggleCreate(true)">new user</button>
					</div>
				</form>
			</div>
			<div class="menu__inner" v-else-if="loginUser">
				<form class="form menu__form">
					<div class="form__row">
						<button class="button" @click.prevent="toggleNewPost(true)">new post</button>
					</div>
					<div class="form__row">
						<button class="button" @click.prevent="toggleFollows(true)">follows</button>
					</div>
					<div class="form__row">
						<button class="button" @click.prevent="toggleSetting(true)">setting</button>
					</div>
					<div class="form__row">
						<button class="button form__button" @click.prevent="logout">logout</button>
					</div>
				</form>
			</div>
		</div>

		<Transition name="popup">
			<div class="popup" v-if="loginCheck && !loginUser && showLogin">
				<div class="popup__box">
					<div class="popup__bar">
						login
						<button class="popup__close" @click="toggleLogin(false)">
							<span></span>
							<span></span>
						</button>
					</div>
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
			</div>
		</Transition>

		<Transition name="popup">
			<div class="popup" v-if="loginCheck && !loginUser && showCreate">
				<div class="popup__box">
					<div class="popup__bar">
						new user
						<button class="popup__close" @click="toggleCreate(false)">
							<span></span>
							<span></span>
						</button>
					</div>
					<form class="form menu__form" @submit.prevent="create">
						<div class="form__row">
							<label for="createUsername">username</label>
							<input id="createUsername" v-model="createUsername"/>
						</div>
						<div class="form__row">
							<label for="createPassword">password</label>
							<input id="createPassword" type="password" v-model="createPassword"/>
						</div>
						<div class="form__row">
							<label for="createConfirm">confirm</label>
							<input id="createConfirm" type="password" v-model="createConfirm"/>
						</div>
						<div class="form__row">
							<button class="button form__button">create</button>
						</div>
					</form>
				</div>
			</div>
		</Transition>

		<Transition name="popup">
			<div class="popup" v-if="loginCheck && loginUser && showNewPost">
				<div class="popup__box">
					<div class="popup__bar">
						new post
						<button class="popup__close" @click="toggleNewPost(false)">
							<span></span>
							<span></span>
						</button>
					</div>
					<form class="form menu__form" @submit.prevent="postPost">
						<div class="form__row">
							<label for="image">image</label>
							<input id="image" type="file" @change="setPostImage" ref="imageInput"/>
						</div>
						<div class="form__row">
							<label for="visibility">visibility</label>
							<select name="visibility" v-model="postVisibility">
								<option value="ALL">All</option>
								<option value="SELF">Self</option>
							</select>
						</div>
						<div class="form__row">
							<label for="content">content</label>
							<input id="content" v-model="postContent"/>
						</div>
						<div class="form__row">
							<button class="button form__button">post</button>
						</div>
					</form>
				</div>
			</div>
		</Transition>

		<Transition name="popup">
			<div class="popup" v-if="showFollows">
				<div class="popup__box">
					<div class="popup__bar">
						follows
						<button class="popup__close" @click="toggleFollows(false)">
							<span></span>
							<span></span>
						</button>
					</div>
					<div>
						<p v-if="followings === null">fetching follows</p>
						<p v-else-if="followings.length === 0">no follows</p>
						<ul v-else class="follows">
							<li v-for="following in followings" class="follows__follow">
								{{following.username}}
								<button class="follows__delete" @click="unfollow(following)">
									<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 8 8"><g fill-rule="evenodd"><path d="M2.048.77l5.18 5.182L5.953 7.23.77 2.048 2.048.77z"/><path d="M5.952.77L7.23 2.05 2.048 7.23.77 5.952 5.953.772z"/></g></svg>
								</button>
							</li>
						</ul>
					</div>
				</div>
			</div>
		</Transition>

		<Transition name="popup">
			<div class="popup" v-if="showSetting">
				<div class="popup__box">
					<div class="popup__bar">
						setting
						<button class="popup__close" @click="toggleSetting(false)">
							<span></span>
							<span></span>
						</button>
					</div>
					<form class="form menu__form" @submit.prevent="changeUsername">
						<div>change username</div>
						<div class="form__row">
							<label for="changeUsernameNew">new username</label>
							<input id="changeUsernameNew" v-model="changeUsernameNew"/>
						</div>
						<div class="form__row">
							<button class="button form__button">change</button>
						</div>
					</form>
					<form class="form menu__form" @submit.prevent="changePassword">
						<div>change password</div>
						<div class="form__row">
							<label for="changePasswordOld">old assword</label>
							<input id="changePasswordOld" type="password" v-model="changePasswordOld"/>
						</div>
						<div class="form__row">
							<label for="changePasswordNew">new password</label>
							<input id="changePasswordNew" type="password" v-model="changePasswordNew"/>
						</div>
						<div class="form__row">
							<label for="changePasswordConfirm">confirm</label>
							<input id="changePasswordConfirm" type="password" v-model="changePasswordConfirm"/>
						</div>
						<div class="form__row">
							<button class="button form__button">change</button>
						</div>
					</form>
				</div>
			</div>
		</Transition>
	`,
	data() {
		return {
			menu: null,

			message: null,
			messageTimeout: null,

			post: null,
			comment: null,
			currentComment: 0,
			moreComments: true,
			fetchingPost: false,

			liking: false,
			deleting: false,

			tag: null,
			posts: null,
			currentPost: 0,
			morePosts: true,
			fetchingPosts: false,

			postImage: null,
			postContent: null,
			postVisibility: "ALL",

			showMenu: false,
			showLogin: false,
			showCreate: false,
			showNewPost: false,
			showFollows: false,
			showSetting: false,
			loginCheck: false,
			loginUser: null,
			username: null,
			password: null,
			createUsername: null,
			createPassword: null,
			createConfirm: null,
			changingPassword: false,
			changePasswordOld: null,
			changePasswordNew: null,
			changePasswordConfirm: null,

			changingUsername: false,
			changeUsernameNew: null,

			followings: null,
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
			this.messageTimeout = setTimeout(() => this.messageTimeout = 0, 5000);
		},
		hideMessage() {
			clearTimeout(this.messageTimeout);
			this.messageTimeout = 0;
		},
		fetchPosts() {
			if (this.fetchingPosts) return;
			this.fetchingPosts = true;

			fetch("/api/v1/post/list?" + new URLSearchParams({
				start: this.currentPost,
				length: 5,
				commentLength: 5,
				...this.tag && {tag: this.tag},
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
					}).map(post => post.hashtags = this.hashtagify(post.content));
					this.checkLike(this.posts.filter((post, index) => {
						return index >= from;
					}));
				})
				.catch(console.error)
				.finally(() => this.fetchingPosts = false);
		},
		fetchPost(post) {
			if (this.fetchingPost) return;
			this.fetchingPost = true;

			fetch(`/api/v1/post/${post.id}`)
				.then(res => res.json())
				.then(json => {
					this.post = json;
					this.post.hashtags = this.hashtagify(this.post.content);
					this.currentComment = this.post.comments.length;

					this.moreComments = true;
					this.fetchComments(this.post);
					this.checkLike([this.post]);
					this.checkFollow(this.post);

					const p = this.posts.find(p => p.id === post.id);
					p.views = json.views;
				})
				.catch(console.error)
				.finally(() => this.fetchingPost = false);
		},
		hashtagify(content) {
			let hashtags = [];
			let ending;
			while (true) {
				let index = content.search(/\#[a-zA-Z0-9]+[a-zA-Z0-9\-\_]*[a-zA-Z0-9]+/);
				if (!~index) break;
				let text = content.slice(0, index);
				let length = content.slice(index + 1).search(/[^a-zA-Z0-9\-\_]/) + 1;
				let tag;
				if (length > 0) {
					tag = content.slice(index, index + length);
					content = content.slice(index + length);
				} else {
					tag = content.slice(index);
					content = "";
				}
				hashtags.push({text, tag});
			}
			hashtags.push({text: content, tag: null});
			return hashtags;
		},
		changeTag(tag) {
			this.tag = tag;
			this.morePosts = true;
			this.posts = null;
			this.post = null;
			this.currentPost = 0;
			this.fetchPosts();
		},
		checkLike(posts) {
			if (!posts) return;

			fetch("/api/v1/post/like?" + new URLSearchParams({
				posts: posts.map(post => post.id),
			}))
				.then(res => {
					if (res.status === 200) return res.json();

					this.posts.forEach(post => post.like = false);
				})
				.then(json => {
					if (!json) return;

					json.likes.forEach(like => {
						const post = posts.find(post => post.id === like.post);
						post.like = like.liked;
						post.likes = like.likes;
					});
				})
				.catch(console.error);
		},
		checkFollow(post) {
			if (!post) return;

			fetch("/api/v1/user/follow?" + new URLSearchParams({user: post.poster.id}))
				.then(res => res.json())
				.then(json => post.following = json === true)
				.catch(console.error);
		},
		fetchComments(post) {
			if (this.fetchingComments) return;
			this.fetchingComments = true;

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
				.catch(console.error)
				.finally(() => this.fetchingComments = false);
		},
		likePost(post) {
			if (this.liking) return;
			this.liking = true;

			fetch("/api/v1/post/like", {
				method: "PUT",
				headers: {
					"Content-Type": "application/json",
				},
				body: JSON.stringify({
					post: post.id,
				}),
			})
				.then(res => {
					if (res.status === 200) return res.json();
					else if (res.status === 500) return res.json();
				})
				.then(json => {
					if (!json) return;
					if (json.message) return this.showMessage(json.message);

					post.like = json.liked;
					post.likes = json.likes;

					const p = this.posts.find(post => post.id === json.post);
					p.like = json.liked;
					p.likes = json.likes;
				})
				.catch(console.error)
				.finally(() => this.liking = false);
		},
		deletePost(post) {
			if (this.deleting) return;
			this.deleting = true;

			fetch("/api/v1/post/delete", {
				method: "DELETE",
				headers: {
					"Content-Type": "application/json",
				},
				body: JSON.stringify({
					post: post.id,
				}),
			})
				.then(res => res.text())
				.then(text => {
					if (text === "true") {
						const index = this.posts.indexOf(post);
						if (~index) this.posts.splice(index, 1);
					}
				})
				.catch(console.error)
				.finally(() => this.deleting = false);
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
		deleteComment(post, comment) {
			fetch("/api/v1/comment/delete", {
				method: "DELETE",
				headers: {
					"Content-Type": "application/json",
				},
				body: JSON.stringify({
					comment: comment.id,
				}),
			})
				.then(res => res.text())
				.then(text => {
					if (text === "true") {
						this.post.comments.length = 0;
						this.currentComment = 0;
						this.moreComments = true;
						this.fetchComments(post);
					}
				})
				.catch(console.error);
		},
		follow(post) {
			const following = post.following;
			fetch("/api/v1/user/follow", {
				method: following? "DELETE": "PUT",
				headers: {
					"Content-Type": "application/json",
				},
				body: JSON.stringify({
					user: post.poster.id,
				}),
			})
				.then(res => res.json())
				.then(json => {
					if (json === "true") {
						if (following) {
							this.showMessage("unfollowed");
							post.following = false;
						} else {
							this.showMessage("followed");
							post.following = true;
						}
					} else if (json.message) {
						this.showMessage(json.message);
					}
				})
				.catch(console.error);
		},
		unfollow(user) {
			fetch("/api/v1/user/follow", {
				method: "DELETE",
				headers: {
					"Content-Type": "application/json",
				},
				body: JSON.stringify({
					user: user.id,
				}),
			})
				.then(res => res.json())
				.then(json => {
					if (json === true) {
						this.showMessage("unfollowed");
						this.fetchFollowings();
					}
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
				[JSON.stringify({
					content: this.postContent,
					visibility: this.postVisibility,
				})],
				{type: "application/json"},
			));
			fetch("/api/v1/post/post", {
				method: "POST",
				body: formData,
			})
				.then(res => res.text())
				.then(text => {
					if (/^-?\d+$/.test(text)) {
						this.postImage = null;
						this.postContent = null;
						this.$refs.imageInput.value = null;
						this.toggleNewPost(false);

						this.morePosts = true;
						this.posts = null;
						this.currentPost = 0;
						this.fetchPosts();

						this.showMessage("post success");
					} else {
						this.showMessage("post fail");
					}
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
					this.checkLike(this.posts);
					this.loginCheck = true;
				})
				.catch(console.error);
		},
		toggleMenu() {
			this.showMenu = !this.showMenu;
		},
		toggleLogin(show) {
			this.showLogin = show;
		},
		toggleCreate(show) {
			this.showCreate = show;
		},
		toggleNewPost(show) {
			this.showNewPost = show;
		},
		create() {
			if (this.createPassword !== this.createConfirm) return;
			fetch("/api/v1/user/create", {
				method: "POST",
				headers: {
					"Content-Type": "application/json",
				},
				body: JSON.stringify({
					username: this.createUsername,
					password: this.createPassword,
				}),
			})
				.then(res => res.text())
				.then(text => {
					if (/^-?\d+$/.test(text)) {
						this.showMessage("create success");
						this.username = this.createUsername;
						this.password = this.createPassword;
						this.createUsername = null;
						this.createPassword = null;
						this.createConfirm = null;
						this.menu = false;
						this.login();
					} else {
						this.showMessage("create fail");
					}
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
						this.username = null;
						this.password = null;
						this.toggleLogin(false);
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
		toggleFollows(show) {
			this.showFollows = show;
			if (show) this.fetchFollowings();
		},
		fetchFollowings() {
			this.followings = null;
			fetch("/api/v1/user/followings?" + new URLSearchParams({
				user: 0,
			}))
				.then(res => res.json())
				.then(json => {
					this.followings = json.followings;
				})
				.catch(console.error);
		},
		toggleSetting(show) {
			this.showSetting = show;
		},
		changePassword() {
			if (this.changingPassword) return;
			this.changingPassword = true;

			fetch("/api/v1/user/password", {
				method: "PUT",
				headers: {
					"Content-Type": "application/json",
				},
				body: JSON.stringify({
					oldPassword: this.changePasswordOld,
					newPassword: this.changePasswordNew,
					confirmPassword: this.changePasswordConfirm,
				}),
			})
				.then(res => res.json())
				.then(json => {
					if (json === true) {
						this.changePasswordOld = null;
						this.changePasswordNew = null;
						this.changePasswordConfirm = null;
						this.showMessage("password change success");
					} else {
						if (json.message) {
							this.showMessage(json.message.toLowerCase());
						} else {
							this.showMessage("password change fail");
						}
					}
				})
				.catch(console.error)
				.finally(() => this.changingPassword = false);
		},
		changeUsername() {
			if (this.changingUsername) return;
			this.changingUsername = true;

			fetch("/api/v1/user/username", {
				method: "PUT",
				headers: {
					"Content-Type": "application/json",
				},
				body: JSON.stringify({
					username: this.changeUsernameNew,
				}),
			})
				.then(res => res.json())
				.then(json => {
					if (json === true) {
						this.showMessage("username change success");
					} else {
						if (json.message) {
							this.changeUsernameNew = null;
							this.showMessage(json.message.toLowerCase());
						} else {
							this.showMessage("username change fail");
						}
					}
				})
				.catch(console.error)
				.finally(() => this.changingUsername = false);
		},
	},
}).mount("#app");
